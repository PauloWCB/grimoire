package com.example.ui.components

import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun ThreeJsDiceView(
    modifier: Modifier = Modifier,
    isRolling: Boolean = false,
    dieType: String = "D20",
    onTapRoll: (() -> Unit)? = null
) {
    val webViewRef = remember { mutableMapOf<String, WebView?>() }

    LaunchedEffect(isRolling) {
        if (isRolling) {
            webViewRef["webView"]?.evaluateJavascript("if(window.startRoll) window.startRoll();", null)
        }
    }

    LaunchedEffect(dieType) {
        webViewRef["webView"]?.evaluateJavascript("if(window.changeDie) window.changeDie('$dieType');", null)
    }

    val htmlContent = remember(dieType) {
        getThreeJsHtml(dieType)
    }

    Box(modifier = modifier) {
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        allowFileAccess = true
                        allowContentAccess = true
                        databaseEnabled = true
                        loadWithOverviewMode = true
                        useWideViewPort = true
                    }
                    setBackgroundColor(android.graphics.Color.TRANSPARENT)
                    setLayerType(View.LAYER_TYPE_HARDWARE, null)
                    webChromeClient = WebChromeClient()
                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            if (isRolling) {
                                view?.evaluateJavascript("if(window.startRoll) window.startRoll();", null)
                            }
                        }
                    }
                    loadDataWithBaseURL("https://ajax.googleapis.com", htmlContent, "text/html", "UTF-8", null)
                    webViewRef["webView"] = this
                }
            },
            update = { view ->
                if (isRolling) {
                    view.evaluateJavascript("if(window.startRoll) window.startRoll();", null)
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

private fun getThreeJsHtml(dieType: String): String {
    return """
<!DOCTYPE html>
<html lang="pt-BR">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
<script src="https://ajax.googleapis.com/ajax/libs/threejs/r125/three.min.js"></script>
<style>
  * { margin: 0; padding: 0; box-sizing: border-box; }
  html, body {
    width: 100%;
    height: 100%;
    overflow: hidden;
    background: transparent;
  }
  #container {
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
  }
</style>
</head>
<body>
<div id="container"></div>
<script>
(function() {
  const container = document.getElementById('container');
  const width = container.clientWidth || window.innerWidth;
  const height = container.clientHeight || window.innerHeight;

  const scene = new THREE.Scene();
  const camera = new THREE.PerspectiveCamera(70, width / height, 0.1, 1000);
  const renderer = new THREE.WebGLRenderer({ alpha: true, antialias: true });

  renderer.setSize(width, height);
  renderer.setPixelRatio(Math.min(window.devicePixelRatio || 1, 2));
  container.appendChild(renderer.domElement);

  let currentGeometry;
  
  function getDieGeometry(type) {
    switch((type || 'D20').toUpperCase()) {
      case 'D4':
        return new THREE.TetrahedronGeometry(2.1, 0);
      case 'D6':
        return new THREE.BoxGeometry(2.2, 2.2, 2.2);
      case 'D8':
        return new THREE.OctahedronGeometry(2.1, 0);
      case 'D12':
        return new THREE.DodecahedronGeometry(1.9, 0);
      case 'D20':
      default:
        return new THREE.IcosahedronGeometry(2.0, 0);
    }
  }

  currentGeometry = getDieGeometry('$dieType');

  // Dark Fantasy Procedural Texture (Obsidian with Blood Veins & Ancient Rune Gold Grain)
  function createDarkFantasyTexture() {
    const canvas = document.createElement('canvas');
    canvas.width = 512;
    canvas.height = 512;
    const ctx = canvas.getContext('2d');

    // Base dark obsidian / onyx gradient
    const grad = ctx.createRadialGradient(256, 256, 30, 256, 256, 360);
    grad.addColorStop(0, '#221a24');
    grad.addColorStop(0.5, '#141018');
    grad.addColorStop(1, '#09070c');
    ctx.fillStyle = grad;
    ctx.fillRect(0, 0, 512, 512);

    // Dark crimson noise / blood veins
    ctx.fillStyle = 'rgba(70, 12, 22, 0.4)';
    for (let i = 0; i < 50; i++) {
      ctx.beginPath();
      const x = Math.random() * 512;
      const y = Math.random() * 512;
      const r = Math.random() * 90 + 15;
      ctx.arc(x, y, r, 0, Math.PI * 2);
      ctx.fill();
    }

    // Eldritch gold & crimson crackle lines
    for (let i = 0; i < 20; i++) {
      ctx.strokeStyle = (i % 2 === 0) ? 'rgba(212, 175, 55, 0.35)' : 'rgba(200, 30, 30, 0.35)';
      ctx.lineWidth = Math.random() * 2 + 1;
      ctx.beginPath();
      let x = Math.random() * 512;
      let y = Math.random() * 512;
      ctx.moveTo(x, y);
      for (let j = 0; j < 5; j++) {
        x += (Math.random() - 0.5) * 120;
        y += (Math.random() - 0.5) * 120;
        ctx.lineTo(x, y);
      }
      ctx.stroke();
    }

    return new THREE.CanvasTexture(canvas);
  }

  const diceTexture = createDarkFantasyTexture();

  // Dark Fantasy Material (Obsidian / Metalness with High Shininess)
  const material = new THREE.MeshStandardMaterial({
      color: 0x231d28,
      roughness: 0.25,
      metalness: 0.85,
      map: diceTexture,
      flatShading: true
  });

  let die = new THREE.Mesh(currentGeometry, material);
  scene.add(die);

  // Engraved Edges (Antique Gold with Dark Crimson undertone)
  const edges = new THREE.EdgesGeometry(currentGeometry);
  const lineMaterial = new THREE.LineBasicMaterial({ color: 0xd4af37, linewidth: 2 });
  let wireframe = new THREE.LineSegments(edges, lineMaterial);
  die.add(wireframe);

  // Atmospheric Dark Fantasy Lighting
  const ambientLight = new THREE.AmbientLight(0x221122, 0.8);
  scene.add(ambientLight);

  // Top-Right Fiery Crimson Light
  const pointLight1 = new THREE.PointLight(0xff3300, 2.2, 50);
  pointLight1.position.set(6, 7, 6);
  scene.add(pointLight1);

  // Bottom-Left Eldritch Gold Light
  const pointLight2 = new THREE.PointLight(0xffaa00, 1.8, 50);
  pointLight2.position.set(-6, -6, -4);
  scene.add(pointLight2);

  // Top-Left Deep Violet Rim Light
  const pointLight3 = new THREE.PointLight(0x9922ff, 1.2, 50);
  pointLight3.position.set(-5, 6, -3);
  scene.add(pointLight3);

  camera.position.z = 5.2;

  let isRolling = false;
  let rollSpeedX = 0;
  let rollSpeedY = 0;
  let rollSpeedZ = 0;

  window.startRoll = function() {
      isRolling = true;
      rollSpeedX = Math.random() * 0.35 + 0.18;
      rollSpeedY = Math.random() * 0.35 + 0.18;
      rollSpeedZ = Math.random() * 0.25 + 0.12;
      setTimeout(() => {
          isRolling = false;
      }, 1500);
  };

  window.changeDie = function(type) {
      scene.remove(die);
      currentGeometry.dispose();
      currentGeometry = getDieGeometry(type);
      die = new THREE.Mesh(currentGeometry, material);
      const newEdges = new THREE.EdgesGeometry(currentGeometry);
      wireframe = new THREE.LineSegments(newEdges, lineMaterial);
      die.add(wireframe);
      scene.add(die);
  };

  window.addEventListener('message', (event) => {
      if (event.data && (event.data.type === 'START_ROLL' || event.data === 'START_ROLL')) {
          window.startRoll();
      }
  });

  function animate() {
      requestAnimationFrame(animate);

      if (isRolling) {
          die.rotation.x += rollSpeedX;
          die.rotation.y += rollSpeedY;
          die.rotation.z += rollSpeedZ;
          rollSpeedX *= 0.975;
          rollSpeedY *= 0.975;
          rollSpeedZ *= 0.975;
      } else {
          die.rotation.x += 0.006;
          die.rotation.y += 0.009;
      }

      renderer.render(scene, camera);
  }

  animate();

  window.addEventListener('resize', () => {
      const w = container.clientWidth || window.innerWidth;
      const h = container.clientHeight || window.innerHeight;
      camera.aspect = w / h;
      camera.updateProjectionMatrix();
      renderer.setSize(w, h);
  });
})();
</script>
</body>
</html>
    """.trimIndent()
}
