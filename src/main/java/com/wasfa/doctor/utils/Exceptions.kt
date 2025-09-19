package com.wasfa.doctor.utils

import java.io.IOException


class ApiException(message: String) : IOException(message)
class NoInternetException(message: String) : IOException(message)
class ErrorBodyException(errorResponse: String?) : IOException(errorResponse)