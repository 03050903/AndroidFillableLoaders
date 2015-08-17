/*
 * Copyright (C) 2015 Jorge Castillo Pérez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jorgecastillo.clippingtransforms;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Region;
import android.view.View;
import java.util.Random;

/**
 * @author jorge
 * @since 12/08/15
 */
public class WavesClippingTransform implements ClippingTransform {

  private int width, height;
  private Path wavesPath;
  private int currentWave = 0;

  @Override public void transform(Canvas canvas, float currentFillPhase, View view) {
    cacheDimensions(view.getWidth(), view.getHeight());
    buildClippingPath();
      //每次刷新都调整 最上面线条的高度(通过fillPhase比例来确定的占高度的比例)
    wavesPath.offset(0, height * -currentFillPhase);
    canvas.clipPath(wavesPath, Region.Op.DIFFERENCE);//剪切的方式，可以用其他值试试
  }

  private void cacheDimensions(int width, int height) {
    if (this.width == 0 || this.height == 0) {
      this.width = width;
      this.height = height;
    }
  }

    /**
     * 路径的点的确定
     */
  private void buildClippingPath() {
    wavesPath = new Path();
    buildWaveAtIndex(currentWave++ % 128, 128);
  }

  private void buildWaveAtIndex(int index, int waveCount) {

    float startingHeight = height - 20;//初始化的高度
    boolean initialOrLast = (index == 1 || index == waveCount);

    float xMovement = (width * 1f / waveCount) * index;//每次X轴移动的距离 -- 数据是随意的
    float divisions = 8; //宽度分割成几个模块 -- 贝塞尔曲线的控制点在 width/8的位置点上
    float variation = 10;//每次Y轴移动的距离

    wavesPath.moveTo(-width, startingHeight);//左上角的点 -- 将位置左移动了 width的距离，应该是为了波形的完整考虑的

    // First wave
    if (!initialOrLast) {//判断是起始点还是最高点 -- 主要是将波形最高点的高度
      variation = randomFloat(); //随机的一个高度
    }

     //第一个前半波形的控制点在 width/8的位置,末端位置在 width/4的位置。高度最高点的不行高度为10
    wavesPath.quadTo(-width + width * 1f / divisions + xMovement, startingHeight + variation,
        -width + width * 1f / 4 + xMovement, startingHeight);

    if (!initialOrLast) {
      variation = randomFloat();
    }
      //第一个后半波形的 控制点和末端点的确定
    wavesPath.quadTo(-width + width * 1f / divisions * 3 + xMovement, startingHeight - variation,
        -width + width * 1f / 2 + xMovement, startingHeight);

    // Second wave
    if (!initialOrLast) {
      variation = randomFloat();
    }

    wavesPath.quadTo(-width + width * 1f / divisions * 5 + xMovement, startingHeight + variation,
        -width + width * 1f / 4 * 3 + xMovement, startingHeight);

    if (!initialOrLast) {
      variation = randomFloat();
    }

    wavesPath.quadTo(-width + width * 1f / divisions * 7 + xMovement, startingHeight - variation,
        -width + width + xMovement, startingHeight);

    // Third wave
    if (!initialOrLast) {
      variation = randomFloat();
    }

      //第三个波形开始 起始点和控制点都在 正值方向
    wavesPath.quadTo(width * 1f / divisions + xMovement, startingHeight + variation,
        width * 1f / 4 + xMovement, startingHeight);

    if (!initialOrLast) {
      variation = randomFloat();
    }

    wavesPath.quadTo(width * 1f / divisions * 3 + xMovement, startingHeight - variation,
        width * 1f / 2 + xMovement, startingHeight);

    // Forth wave
    if (!initialOrLast) {
      variation = randomFloat();
    }

    wavesPath.quadTo(width * 1f / divisions * 5 + xMovement, startingHeight + variation,
        width * 1f / 4 * 3 + xMovement, startingHeight);

    if (!initialOrLast) {
      variation = randomFloat();
    }

    wavesPath.quadTo(width * 1f / divisions * 7 + xMovement, startingHeight - variation,
        width + xMovement, startingHeight);

    // Closing path 100的作用是多绘制100距离，保证波形动画在可见范围内是完整的
    wavesPath.lineTo(width + 100, startingHeight); //右上角的点
    wavesPath.lineTo(width + 100, 0); //右下角的点
    wavesPath.lineTo(0, 0); //左下角点
    wavesPath.close();
  }

  private float randomFloat() {
    return nextFloat(10) + height * 1f / 25;
  }

  private float nextFloat(float upperBound) {
    Random random = new Random();
    return (Math.abs(random.nextFloat()) % (upperBound + 1));
  }
}
