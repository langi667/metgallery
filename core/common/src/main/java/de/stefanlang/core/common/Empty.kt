package de.stefanlang.core.common

object Empty {
    @Suppress("unused")
    fun allNull(vararg args: Any?): Boolean {
        val firstNotNull = args.find {currArg ->
            currArg != null
        }

        val retVal = firstNotNull == null
        return retVal
    }

    fun allNullOrBlank(vararg args: Any?): Boolean {
        val firstNotNull = args.find {currArg ->
            var retVal = currArg != null

            if (currArg is String){
                retVal = currArg.isNotBlank()
            }

            retVal
        }

        val retVal = firstNotNull == null
        return retVal
    }
}