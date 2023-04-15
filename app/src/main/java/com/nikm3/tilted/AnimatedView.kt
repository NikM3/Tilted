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

    init {
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

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        newGoal()
    }

    override fun onDraw(canvas: Canvas) {
        makeGoal(canvas)
        checkBounds()
        puckDrawable.setBounds(
            puckX,
            puckY,
            puckX + puckWidth,
            puckY + puckHeight
        )
        puckDrawable.draw(canvas)
        checkGoal()
        invalidate()
    }

    private fun newGoal() {
        goalX = Random.nextInt(1, width - goalWidth - 1)
        goalY = Random.nextInt(1, height - goalHeight - 1)
    }

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
            score++
            newGoal()
        }
    }

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

    fun updatePuck(newX: Int, newY: Int) {
        puckX -= newX
        puckY += newY
    }

    companion object {
        const val puckWidth = 50
        const val puckHeight = 50
    }
}