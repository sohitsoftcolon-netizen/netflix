package com.example.adblock

import android.net.Uri
import android.webkit.WebResourceResponse
import java.io.ByteArrayInputStream

object AdBlocker {

    // Common ad networks, tracker domains, popup scripts, and malicious redirects
    private val AD_DOMAINS = setOf(
        "doubleclick.net",
        "googlesyndication.com",
        "googleadservices.com",
        "adservice.google.com",
        "pagead2.googlesyndication.com",
        "adnxs.com",
        "exoclick.com",
        "popads.net",
        "propellerads.com",
        "monetag.com",
        "adsterra.com",
        "clickadu.com",
        "hilltopads.com",
        "richpush.co",
        "trafficjunky.com",
        "outbrain.com",
        "taboola.com",
        "adcash.com",
        "adform.net",
        "bidswitch.net",
        "criteo.com",
        "rubiconproject.com",
        "amazon-adsystem.com",
        "scorecardresearch.com",
        "smartadserver.com",
        "zedo.com",
        "zergnet.com",
        "mgid.com",
        "infolinks.com",
        "adcolony.com",
        "unityads.unity3d.com",
        "applovin.com",
        "chartboost.com",
        "vungle.com",
        "admob.com",
        "serving-sys.com",
        "pubmatic.com",
        "moatads.com",
        "openx.net",
        "quantserve.com",
        "ad-delivery.net",
        "syndication.exdynsrv.com",
        "tsyndicate.com",
        "trafficstars.com",
        "juicyads.com",
        "popcash.net",
        "admaven.com",
        "adtrue.com",
        "yieldmo.com",
        "sovrn.com",
        "casalemedia.com",
        "lijit.com",
        "revcontent.com",
        "media.net",
        "adroll.com"
    )

    private val AD_URL_PATTERNS = listOf(
        "/ads/",
        "/ad/",
        "/banner/",
        "/banners/",
        "adsbygoogle",
        "pagead",
        "popunder",
        "pop-ad",
        "pop_ad",
        "adserver",
        "ad_type=",
        "ad_system",
        "clickjack",
        "sponsored-content",
        "interstitial"
    )

    fun isAd(url: String): Boolean {
        if (url.isBlank()) return false
        try {
            val uri = Uri.parse(url)
            val host = uri.host?.lowercase() ?: return false

            // Check known host or subdomains
            for (domain in AD_DOMAINS) {
                if (host == domain || host.endsWith(".$domain")) {
                    return true
                }
            }

            // Check URL path patterns
            val path = uri.path?.lowercase() ?: ""
            val query = uri.query?.lowercase() ?: ""
            val fullPath = "$path?$query"

            for (pattern in AD_URL_PATTERNS) {
                if (fullPath.contains(pattern)) {
                    // Do not block legitimate site pages that may contain 'ad' in path
                    if (host.contains("mwsguy.sbs")) {
                        continue
                    }
                    return true
                }
            }
        } catch (_: Exception) {
            return false
        }
        return false
    }

    fun createBlockedResponse(): WebResourceResponse {
        return WebResourceResponse(
            "text/plain",
            "UTF-8",
            ByteArrayInputStream(ByteArray(0))
        )
    }

    /**
     * Injected CSS to hide ad blocks, floating banners, and fake overlay buttons
     */
    fun getAdHidingCss(): String {
        return """
            [id*='google_ads'], [class*='adsbygoogle'], [class*='ad-banner'],
            [id*='ad-banner'], iframe[src*='ads'], iframe[id*='ad'], iframe[name*='ad'],
            div[class*='ad_'], div[id*='ad_'], .popup-overlay, #pop-ad, .banner-ad,
            div[class*='banner-wrap'], div[id*='advertisement'], div[class*='advertisement'],
            .interstitial, .popunder, a[href*='bet365'], a[href*='1xbet'],
            div[class*='ad-container'], div[id*='ad-container'],
            .ad-wrapper, #ad-wrapper, .adbox, #adbox, .ad_slot, .ads-holder {
                display: none !important;
                visibility: hidden !important;
                height: 0 !important;
                max-height: 0 !important;
                opacity: 0 !important;
                pointer-events: none !important;
            }
        """.trimIndent().replace("\n", " ")
    }

    /**
     * Injected JS to enforce popup blocking and remove dynamic overlays
     */
    fun getAdBlockingScript(): String {
        val css = getAdHidingCss().replace("'", "\\'")
        return """
            (function() {
                // Inject hiding styles
                var style = document.getElementById('mwsguy-adshield-style');
                if (!style) {
                    style = document.createElement('style');
                    style.id = 'mwsguy-adshield-style';
                    style.type = 'text/css';
                    style.innerHTML = '$css';
                    if (document.head) {
                        document.head.appendChild(style);
                    } else if (document.body) {
                        document.body.appendChild(style);
                    }
                }
                
                // Override window.open to prevent popup spam
                window.open = function(url) {
                    console.log('Blocked popup window to: ' + url);
                    return null;
                };

                // Remove ad elements if they appear dynamically
                function cleanAds() {
                    var selectors = [
                        '.popup-overlay', '#pop-ad', '.modal-ad',
                        'iframe[src*="ad"]', 'iframe[src*="syndication"]',
                        '[class*="adsbygoogle"]'
                    ];
                    selectors.forEach(function(sel) {
                        try {
                            var els = document.querySelectorAll(sel);
                            els.forEach(function(el) {
                                if (el && el.parentNode) {
                                    el.parentNode.removeChild(el);
                                }
                            });
                        } catch (e) {}
                    });
                }
                cleanAds();
                setTimeout(cleanAds, 1000);
                setTimeout(cleanAds, 2500);
            })();
        """.trimIndent()
    }
}
