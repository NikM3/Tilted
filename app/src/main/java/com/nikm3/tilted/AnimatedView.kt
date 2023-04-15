package com.nikm3.tilted

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.util.AttributeSet
import android.view.View
import kotlin.random.Random

class AnimatedView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val goalDrawable = ShapeDrawable(OvalShape())
    private val puckDrawable = ShapeDrawable(OvalShape())
    private var puckX: Int = 0
    private var puckY: Int = 0
    private var goalX: Int = 0
    private var goalY: Int = 0
    private var goalWidth: Int = puckWidth * 4
    private var goalHeight: Int = puckHeight * 4
    var score = 0
    var relativeX = 0.0
    var relativeY = 0.0
    var konfettiTime = false

    /**
     * Constructor stuff, initialize the goal and puck
     */
    init {
        goalDrawable.paint.color = resources.getColor(R.color.purple_200, null)
        puckX = width / 2
        puckY = height / 2
        puckDrawable.paint.color = -0x53dd
        puckDrawable.setBounds(
            puckX,
            puckY,
            puckX + puckWidth,
            puckY + puckHeight
        )
    }

    /**
     * Should only be called the first time the view is drawn,
     * generate coordinates for a new goal.
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        newGoal()
    }

    /**
     * Draw the goal and puck at their current coordinates.
     * Ensure the puck is not leaving the screen before drawing it.
     * Also check if the puck is within the goal.
     */
    override fun onDraw(canvas: Canvas) {
        makeGoal(canvas)
        checkBounds()
        makePuck(canvas)
        checkGoal()
        invalidate()
    }

    /**
     * Generate new coordinates for the goal
     */
    private fun newGoal() {
        goalX = Random.nextInt(1, (width - goalWidth - 1))
        goalY = Random.nextInt(1, (height - goalHeight - 1))
    }

    /**
     * Check if the puck is currently within the bounds of the goal
     */
    private fun checkGoal() {
        val puckTop = puckY
        val puckBot = puckY + puckHeight
        val puckLeft = puckX
        val puckRight = puckX + puckWidth
        val goalTop = goalY
        val goalBot = goalY + goalHeight
        val goalLeft = goalX
        val goalRight = goalX + goalWidth
        if (puckLeft > goalLeft &&
            puckRight < goalRight &&
            puckTop > goalTop &&
            puckBot < goalBot
        ) {
            confetti()
            score++
            newGoal()
        }
    }

    /**
     * Draw the goal
     */
    private fun makeGoal(canvas: Canvas) {
        goalDrawable.setBounds(
            goalX,
            goalY,
            goalX + goalWidth,
            goalY + goalHeight
        )
        goalDrawable.draw(canvas)
        invalidate()
    }

    /**
     * Ensure the puck does not move off of the screen or under the player banner
     */
    private fun checkBounds() {
        val puckTop = puckY
        val puckBot = puckY + puckHeight
        val puckLeft = puckX
        val puckRight = puckX + puckWidth
        if (puckLeft < x) {
            puckX = x.toInt()
        } else if (puckRight > width) {
            puckX = width - puckWidth
        }
        if (puckTop < y - 100) {
            puckY = y.toInt() - 100
        } else if (puckBot > height) {
            puckY = height - puckHeight
        }
    }

    /**
     * Draw the puck
     */
    private fun makePuck(canvas: Canvas) {
        puckDrawable.setBounds(
            puckX,
            puckY,
            puckX + puckWidth,
            puckY + puckHeight
        )
        puckDrawable.draw(canvas)
    }

    /**
     * Update the puck's coordinates with sensor information from Main
     */
    fun updatePuck(newX: Int, newY: Int) {
        puckY += newX
        puckX += newY
    }

    /**
     * Pop some confetti to congratulate the user
     */
    private fun confetti() {
        relativeX = ((puckX + puckWidth * 0.5) / width.toDouble())
        relativeY = ((puckY + puckHeight * 0.5) / height.toDouble())
        konfettiTime = true
    }

    companion object {
        const val puckWidth = 50
        const val puckHeight = 50
    }
}