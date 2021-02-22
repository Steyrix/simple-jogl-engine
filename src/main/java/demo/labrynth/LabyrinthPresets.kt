package demo.labrynth

import engine.feature.animation.BasicAnimation

// TODO: implement
data class LabyrinthPresets(
        val characterPresets: CharacterPresets = CharacterPresets()
)

data class CharacterPresets(
        val animation: AnimationsPresets = AnimationsPresets()
)

data class AnimationsPresets(
        val animations: MutableList<BasicAnimation> = mutableListOf(
                BasicAnimation("WALK", 1, 0, 6, 1, 100f),
                BasicAnimation("JUMP", 2, 0, 3, 1, 200f),
                BasicAnimation("IDLE", 3, 0, 1, 1, 100f)
        )
)