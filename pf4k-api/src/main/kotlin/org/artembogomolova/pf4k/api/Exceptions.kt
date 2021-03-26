package org.artembogomolova.pf4k.api

/**
 * Unknown module exception. Throw when unspecified case or cause for program error.
 *
 * @author bogomolov-a-a
 */
open class BasicModuleException(message: String, cause: Exception? = null) : Exception(message, cause)

class PreconditionCheckedException(message: String, cause: Exception? = null) : BasicModuleException(message, cause)

class ApiDescriptorNotFoundException(message: String, cause: Exception? = null) : BasicModuleException(message, cause)

open class BasicApplicationRuntimeException(message: String, cause: Exception? = null) : BasicModuleException(message, cause)
class CoreModuleStartingException(message: String, cause: Exception? = null) : BasicApplicationRuntimeException(message, cause)