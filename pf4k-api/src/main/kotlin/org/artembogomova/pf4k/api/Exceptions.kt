package org.artembogomova.pf4k.api

/**
 * Unknown module exception. Throw when unspecified case or cause for program error.
 *
 * @author bogomolov-a-a
 */
open class BasicModuleException(message: String, cause: Exception? = null) : Exception(message, cause)

class PreconditionCheckedException(message: String, cause: Exception? = null) : BasicModuleException(message, cause)