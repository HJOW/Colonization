package org.duckdns.hjow.colonization.ui;
/*
import org.duckdns.hjow.colonization.elements.Celestials;
import org.duckdns.hjow.colonization.elements.city.City;
import org.duckdns.hjow.colonization.elements.ship.Ship;

import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.universe.SimpleUniverse;
import javax.media.j3d.*;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
*/
import java.awt.*;

/** 함선 위치 현황 출력을 위한 패널 - Java3D 사용 - java.lang.UnsatisfiedLinkError: no j3dcore-ogl in java.library.path 문제로 당분간 사용 X */
@Deprecated
public class Java3DSpacePanel extends SpacePanel {
	private static final long serialVersionUID = -8262391729757987120L;
	/*
    protected transient Canvas3D canvas;
    protected transient SimpleUniverse universe;
    protected transient BranchGroup scene;
    */
	public Java3DSpacePanel() {
		super();
		setLayout(new BorderLayout());

        // GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        // GraphicsDevice device = env.getDefaultScreenDevice();
        // GraphicsConfiguration configs = device.getDefaultConfiguration();

        // canvas = new Canvas3D(configs);
		/*
        canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        universe = new SimpleUniverse(canvas);
        universe.getViewingPlatform().setNominalViewingTransform();
        scene = new BranchGroup();
        add(canvas, BorderLayout.CENTER);
        */
	}

	@Override
	protected void draw(Graphics g) {
		/*
        double x, y, z, r;
        Color3f color;
        Appearance app;
        Sphere sphere;
        Transform3D transform;
        TransformGroup transGroup;

        scene = new BranchGroup();

        // 사전 준비
        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
        Color3f lightColor = new Color3f(1.0f, 1.0f, 1.0f);
        Vector3f lightDirection = new Vector3f(4.0f, -7.0f, -12.0f);
        DirectionalLight light = new DirectionalLight(lightColor, lightDirection);
        light.setInfluencingBounds(bounds);
        scene.addChild(light);

        // 도시 그리기
        for(City city : colony.getCities()) {
            x = city.getX();
            y = city.getY();
            z = city.getZ();
            r = 10.0;
            color = new Color3f(Color.BLUE);

            // 구 그리기
            app = new Appearance();
            app.setMaterial(new Material(color, color, color, color, 64.0f));
            sphere = new Sphere((float) r, Sphere.GENERATE_NORMALS, 80, app);

            // 위치 설정
            transform = new Transform3D();
            transform.setTranslation(new Vector3d(x, y, z));
            transGroup = new TransformGroup(transform);
            transGroup.addChild(sphere);
            scene.addChild(transGroup);
        }

        // 함선 그리기
        for(Ship ship : colony.getShips()) {
            x = ship.getX();
            y = ship.getY();
            z = ship.getZ();
            r = 5.0;
            color = new Color3f(Color.GREEN);

            // 구 그리기
            app = new Appearance();
            app.setMaterial(new Material(color, color, color, color, 64.0f));
            sphere = new Sphere((float) r, Sphere.GENERATE_NORMALS, 80, app);

            // 위치 설정
            transform = new Transform3D();
            transform.setTranslation(new Vector3d(x, y, z));
            transGroup = new TransformGroup(transform);
            transGroup.addChild(sphere);
            scene.addChild(transGroup);
        }

        // 천체 그리기
        for(Celestials cele : colony.getCelestials()) {
            x = cele.getX();
            y = cele.getY();
            z = cele.getZ();
            r = 8.0;
            color = new Color3f(Color.ORANGE);

            // 구 그리기
            app = new Appearance();
            app.setMaterial(new Material(color, color, color, color, 64.0f));
            sphere = new Sphere((float) r, Sphere.GENERATE_NORMALS, 80, app);

            // 위치 설정
            transform = new Transform3D();
            transform.setTranslation(new Vector3d(x, y, z));
            transGroup = new TransformGroup(transform);
            transGroup.addChild(sphere);
            scene.addChild(transGroup);
        }

        universe.addBranchGraph(scene);
        universe.getViewingPlatform().setNominalViewingTransform();
        */
    }

}
