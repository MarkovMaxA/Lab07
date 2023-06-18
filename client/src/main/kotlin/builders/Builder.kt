package client.builders

interface Builder<ObjectClassBuild> {
    /**
     * Build creates new instance of object
     */
    fun build(): ObjectClassBuild
}