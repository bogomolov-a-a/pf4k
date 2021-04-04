package org.artembogomolova.pf4k.impl.module.management.loading

import java.net.URL
import java.net.URLClassLoader

class LoadableModuleClassLoader(urls: Array<URL>, parent: ClassLoader) : URLClassLoader(urls, parent) {
    /*for lazy add urls*/
    public override fun addURL(url: URL) {
        super.addURL(url)
    }
}