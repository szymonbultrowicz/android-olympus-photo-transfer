package org.szymonbultrowicz.olympusphototransfer.extensions

operator fun Regex.contains(text: CharSequence): Boolean = this.matches(text)