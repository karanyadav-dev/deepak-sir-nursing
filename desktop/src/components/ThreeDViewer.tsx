import { useRef, Suspense } from 'react';
import { Canvas } from '@react-three/fiber';
import { OrbitControls, useGLTF, Environment, ContactShadows, Html } from '@react-three/drei';

interface ThreeDViewerProps {
  modelUrl: string;
}

function Model({ url }: { url: string }) {
  const { scene } = useGLTF(url);
  return <primitive object={scene} scale={1} />;
}

function PlaceholderModel() {
  return (
    <mesh>
      <boxGeometry args={[2, 2, 2]} />
      <meshStandardMaterial color="#c084fc" />
    </mesh>
  );
}

export default function ThreeDViewer({ modelUrl }: ThreeDViewerProps) {
  const controlsRef = useRef<any>(null);

  const is3DFile = modelUrl && (
    modelUrl.endsWith('.glb') ||
    modelUrl.endsWith('.gltf') ||
    modelUrl.startsWith('blob:')
  );

  return (
    <div style={{ width: '100%', height: '100%', position: 'relative' }}>
      <Canvas
        camera={{ position: [0, 0, 5], fov: 50 }}
        style={{ background: 'transparent' }}
        gl={{ alpha: true, antialias: true }}
      >
        <ambientLight intensity={0.7} />
        <directionalLight position={[10, 10, 5]} intensity={1} castShadow />
        <directionalLight position={[-10, -10, -5]} intensity={0.5} />
        <Environment preset="city" />

        <Suspense fallback={
          <Html center>
            <div style={{ color: 'white', fontSize: 14 }}>Loading 3D...</div>
          </Html>
        }>
          {is3DFile ? <Model url={modelUrl} /> : <PlaceholderModel />}
        </Suspense>

        <ContactShadows position={[0, -2, 0]} opacity={0.5} scale={10} blur={2} far={4} />

        <OrbitControls
          ref={controlsRef}
          enablePan={true}
          enableZoom={true}
          enableRotate={true}
          minDistance={2}
          maxDistance={20}
        />
      </Canvas>
    </div>
  );
}