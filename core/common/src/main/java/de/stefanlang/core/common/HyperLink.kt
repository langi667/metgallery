package de.stefanlang.core.common

data class HyperLink(val url: String, val title: String = url) {

    companion object {

        // region Public API
        
        fun createList(vararg args: String?): List<HyperLink> {
            val retVal = mutableListOf<HyperLink>()

            args.forEach { currLink ->
                if (currLink?.isNotBlank() == true) {
                    retVal.add(HyperLink(currLink))
                }
            }

            return retVal
        }

        // endregion
    }



}
