package engine.core.context

import engine.core.entity.Entity

open class EntityMapContext : GameContext {

    protected val entityMap = hashMapOf<String, Entity>()

    override fun init() {
        TODO("Not yet implemented")
    }


}