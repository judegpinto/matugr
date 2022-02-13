package matugr.common.external

/**
 * Generalized "port-adapter scheme" interface. Intended for client to implement for library
 * to launch URLs, e.g. authorization request urls.
 */
interface UrlLauncherPort {
    fun launchUrl(url: String)
}
