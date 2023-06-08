package commmon.builders

interface Builder<ObjectClassBuild> {
    /**
     * Build creates new instance of object
     */
    fun build(): ObjectClassBuild
}