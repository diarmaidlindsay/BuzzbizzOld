package jp.pulseanddecibels.buzbiz.models;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLU;
import android.opengl.GLSurfaceView.Renderer;

/**
 * //OpenGlの描画クラス
 */
public class PageRenderer implements Renderer {

	public Page page;
	private Context context;


	static GL10 gl;
	static int width;
	static int height;





	/*
	 * コンストラクタ
	 */
	public PageRenderer(Context context) {
		this.context = context;

		page = new Page();
	}





	/**
	 * 初期化処理
	 */
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		page.loadGLTexture(gl, this.context);

		//テクスチャの有効化
		gl.glEnable(GL10.GL_TEXTURE_2D);
		// 面の描画をなめらかにするようにします
		gl.glShadeModel(GL10.GL_SMOOTH);

		// 背景を透明に設定
//		gl.glClearColor(0.0f, 0.0f, 0.0f, 0);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);


		// 面の描画をなめらかにするようにします
        gl.glShadeModel(GL10.GL_SMOOTH);

		gl.glClearDepthf(1.0f);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDepthFunc(GL10.GL_LEQUAL);

		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
	}





	/**
	 * 描画処理
	 */
	public void onDrawFrame(GL10 gl) {
		// 背景を透明に設定
//		gl.glClearColor(0.0f, 0.0f, 0.0f, 0);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();

		gl.glTranslatef(0.0f, 0.0f, -2.0f);
		gl.glTranslatef(-0.5f, -0.5f, 0.0f);

		page.draw(gl);
	}





	/**
	 * サーフェスが作成された時,サイズが変わった時の処理
	 */
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		if(height == 0) {
			height = 1;
		}

		// ビューポートの設定
		gl.glViewport(0,		// X座標
				      0,		// Y座標
				      width, 	// ウィンドウの幅
				      height);	// ウィンドウの高さ



		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();

		// 視界を決定する
		GLU.gluPerspective(gl, 28.0f, 1.0f, 0.1f, 100.0f);

		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
	}
}
