package com.kuaibao.skuaidi.activity.scan_mobile.tesseract;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;

public class BitmapUtil {

	/**
	 * 旋转图片
	 *
	 * @param degree
	 *            旋转的角度
	 * @param bitmap
	 *            需要旋转的图片
	 * @return Bitmap 旋转以后的图片
	 */
	public static Bitmap rotaingImageView(int degree, Bitmap bitmap) {
		// 旋转图片 动作
		Matrix matrix = new Matrix();
		;
		matrix.postRotate(degree);
		System.out.println("angle2=" + degree);
		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}

	// 将图片进行灰度化
	public static Bitmap bitmap2Gray(Bitmap bmSrc) {
		// 得到图片的长和宽
		int width = bmSrc.getWidth();
		int height = bmSrc.getHeight();
		// 创建目标灰度图像
		Bitmap bmpGray = null;
		bmpGray = Bitmap.createBitmap(width, height, Config.RGB_565);
		// 创建画布
		Canvas c = new Canvas(bmpGray);
		Paint paint = new Paint();
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
		ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		paint.setColorFilter(f);
		c.drawBitmap(bmSrc, 0, 0, paint);
		return bmpGray;
	}

	// 对图像进行线性灰度变化
	public static Bitmap lineGrey(Bitmap image) {
		// 得到图像的宽度和长度
		int width = image.getWidth();
		int height = image.getHeight();
		// 创建线性拉升灰度图像
		Bitmap linegray = null;
		linegray = image.copy(Config.ARGB_8888, true);
		// 依次循环对图像的像素进行处理
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				// 得到每点的像素值
				int col = image.getPixel(i, j);
				int alpha = col & 0xFF000000;
				int red = (col & 0x00FF0000) >> 16;
				int green = (col & 0x0000FF00) >> 8;
				int blue = (col & 0x000000FF);
				// 增加了图像的亮度
				red = (int) (1.1 * red + 30);
				green = (int) (1.1 * green + 30);
				blue = (int) (1.1 * blue + 30);
				// 对图像像素越界进行处理
				if (red >= 255) {
					red = 255;
				}

				if (green >= 255) {
					green = 255;
				}

				if (blue >= 255) {
					blue = 255;
				}
				// 新的ARGB
				int newColor = alpha | (red << 16) | (green << 8) | blue;
				// 设置新图像的RGB值
				linegray.setPixel(i, j, newColor);
			}
		}
		return linegray;
	}

	// 该函数实现对图像进行二值化处理
	public static Bitmap gray2Binary(Bitmap graymap) {
		graymap = bitmap2Gray(graymap);

		// 得到图形的宽度和长度
		int width = graymap.getWidth();
		int height = graymap.getHeight();
		// 创建二值化图像
		Bitmap binarymap = null;
		binarymap = graymap.copy(Config.ARGB_8888, true);
		// 依次循环，对图像的像素进行处理
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				// 得到当前像素的值
				int col = binarymap.getPixel(i, j);
				// 得到alpha通道的值
				int alpha = col & 0xFF000000;
				// 得到图像的像素RGB的值
				int red = (col & 0x00FF0000) >> 16;
				int green = (col & 0x0000FF00) >> 8;
				int blue = (col & 0x000000FF);
				// 用公式X = 0.3×R+0.59×G+0.11×B计算出X代替原来的RGB
				int gray = (int) ((float) red * 0.3 + (float) green * 0.59 + (float) blue * 0.11);
				// 对图像进行二值化处理
				if (gray <= 95) {
					gray = 0;
				} else {
					gray = 255;
				}
				// 新的ARGB
				int newColor = alpha | (gray << 16) | (gray << 8) | gray;
				// 设置新图像的当前像素值
				binarymap.setPixel(i, j, newColor);
			}
		}
		return binarymap;
	}

	// 图像锐化处理 拉普拉斯算子处理
	public static Bitmap SharpenImage(Bitmap bmp) {
		/*
		 * 锐化基本思想是加强图像中景物的边缘和轮廓,使图像变得清晰 而图像平滑是使图像中边界和轮廓变得模糊
		 *
		 * 拉普拉斯算子图像锐化 获取周围9个点的矩阵乘以模板9个的矩阵 卷积
		 */
		// 拉普拉斯算子模板 { 0, -1, 0, -1, -5, -1, 0, -1, 0 } { -1, -1, -1, -1, 9, -1,
		// -1, -1, -1 }
		int[] laplacian = new int[] { -1, -1, -1, -1, 9, -1, -1, -1, -1 };
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height, Config.RGB_565);
		int pixR = 0;
		int pixG = 0;
		int pixB = 0;
		int pixColor = 0;
		int newR = 0;
		int newG = 0;
		int newB = 0;
		int idx = 0;
		float alpha = 0.3F; // 图片透明度
		int[] pixels = new int[width * height];
		bmp.getPixels(pixels, 0, width, 0, 0, width, height);
		// 图像处理
		for (int i = 1; i < height - 1; i++) {
			for (int k = 1; k < width - 1; k++) {
				idx = 0;
				newR = 0;
				newG = 0;
				newB = 0;
				for (int n = -1; n <= 1; n++) // 取出图像3*3领域像素
				{
					for (int m = -1; m <= 1; m++) // n行数不变 m列变换
					{
						pixColor = pixels[(i + n) * width + k + m]; // 当前点(i,k)
						pixR = Color.red(pixColor);
						pixG = Color.green(pixColor);
						pixB = Color.blue(pixColor);
						// 图像像素与对应摸板相乘
						newR = newR + (int) (pixR * laplacian[idx] * alpha);
						newG = newG + (int) (pixG * laplacian[idx] * alpha);
						newB = newB + (int) (pixB * laplacian[idx] * alpha);
						idx++;
					}
				}
				newR = Math.min(255, Math.max(0, newR));
				newG = Math.min(255, Math.max(0, newG));
				newB = Math.min(255, Math.max(0, newB));
				// 赋值
				pixels[i * width + k] = Color.argb(255, newR, newG, newB);
			}
		}
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
//		imageShow.setImageBitmap(bitmap);
		return bitmap;
	}

	/**获取自定义大小的图片**/
	public static Bitmap getCustomBitmap(Bitmap bitmap ,int heightPx,int widthPx) {
		int x = 0;
		int y = heightPx/3;// 获取y轴开始的高度
		int bitmapWidth = widthPx;
		int bitmapHeight = heightPx/2;
		Bitmap _bitmap = Bitmap.createBitmap(bitmap, x, y, bitmapWidth, bitmapHeight);
		return _bitmap;
	}

}
