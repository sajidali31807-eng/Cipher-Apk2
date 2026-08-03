import React, { useState } from 'react';
import { TabType } from './types';
import { Navbar } from './components/Navbar';
import { FloatingOrb } from './components/FloatingOrb';
import { PermissionWizard } from './components/PermissionWizard';
import { LiveVoiceSimulator } from './components/LiveVoiceSimulator';
import { AccessibilityInspector } from './components/AccessibilityInspector';
import { FunctionToolsMatrix } from './components/FunctionToolsMatrix';
import { VivoDiagnostics } from './components/VivoDiagnostics';
import { RoadmapView } from './components/RoadmapView';
import { CIPHER_PHASES, CIPHER_TOOLS, CIPHER_PERMISSIONS } from './data/cipherData';
import { 
  Radio, 
  Mic, 
  Eye, 
  Wrench, 
  ShieldCheck, 
  Smartphone, 
  Sparkles, 
  Zap, 
  CheckCircle2, 
  Activity,
  ListOrdered,
  ArrowRight
} from 'lucide-react';

export default function App() {
  const [activeTab, setActiveTab] = useState<TabType>('overview');
  const [isOrbVisible, setIsOrbVisible] = useState<boolean>(true);
  const [isListening, setIsListening] = useState<boolean>(false);
  const [currentToolCall, setCurrentToolCall] = useState<string>('');

  const handleTriggerWakeWord = () => {
    setIsOrbVisible(true);
    setIsListening(true);
    setCurrentToolCall('');
    setTimeout(() => {
      setIsListening(false);
      setCurrentToolCall('read_current_screen()');
    }, 1500);
  };

  const handleToolTriggeredFromSim = (toolName: string) => {
    setCurrentToolCall(toolName);
    setIsOrbVisible(true);
  };

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 font-sans selection:bg-cyan-500 selection:text-slate-950">
      {/* Persistent Floating Visual Orb HUD Overlay */}
      <FloatingOrb
        isVisible={isOrbVisible}
        onClose={() => setIsOrbVisible(false)}
        statusText='"Get Ready Cipher"'
        isListening={isListening}
        isThinking={false}
        isSpeaking={false}
        currentToolCall={currentToolCall}
      />

      {/* Main Top Header Navbar */}
      <Navbar
        activeTab={activeTab}
        setActiveTab={setActiveTab}
        isListening={isListening}
        onTriggerWakeWord={handleTriggerWakeWord}
        isOrbVisible={isOrbVisible}
        setIsOrbVisible={setIsOrbVisible}
      />

      {/* Main View Container */}
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* OVERVIEW TAB */}
        {activeTab === 'overview' && (
          <div className="space-y-8">
            {/* Hero Banner */}
            <div className="relative bg-gradient-to-r from-slate-900 via-indigo-950 to-slate-900 border border-slate-800 rounded-3xl p-8 overflow-hidden shadow-2xl">
              <div className="absolute top-0 right-0 -mt-12 -mr-12 w-96 h-96 bg-cyan-500/10 rounded-full blur-3xl pointer-events-none" />
              <div className="absolute bottom-0 left-1/3 -mb-12 w-80 h-80 bg-blue-500/10 rounded-full blur-3xl pointer-events-none" />

              <div className="relative z-10 max-w-3xl space-y-4">
                <div className="inline-flex items-center space-x-2 px-3 py-1 rounded-full bg-cyan-950/80 border border-cyan-800 text-cyan-300 text-xs font-mono">
                  <Radio className="w-3.5 h-3.5 text-cyan-400 animate-pulse" />
                  <span>PROJECT CIPHER MASTER ARCHITECTURE V2.0</span>
                </div>

                <h1 className="text-3xl sm:text-4xl font-extrabold text-white tracking-tight leading-tight">
                  Autonomous Phone AI Assistant Suite
                </h1>

                <p className="text-slate-300 text-sm sm:text-base leading-relaxed">
                  Project Cipher is a fully autonomous Jarvis-like AI assistant running 24/7 as a Foreground Service on Android (Kotlin + Jetpack Compose), optimized specifically for Vivo Y15s (Funtouch OS). Features offline Sherpa-ONNX wake word detection, Gemini Live bilingual voice API, screen accessibility reader, and 21 function calling tools.
                </p>

                {/* Architecture Highlights Badges */}
                <div className="flex flex-wrap gap-2 pt-2">
                  {[
                    { label: 'Sherpa-ONNX Offline KWS', icon: Zap },
                    { label: 'Gemini Live Voice Brain', icon: Mic },
                    { label: 'Accessibility Eyes & Hands', icon: Eye },
                    { label: 'Floating Orb HUD', icon: Activity },
                    { label: 'Offline Fallback Engine', icon: Wrench },
                    { label: 'Vivo Y15s Autostart Ready', icon: Smartphone }
                  ].map((item, idx) => {
                    const Icon = item.icon;
                    return (
                      <span key={idx} className="px-3 py-1.5 rounded-xl bg-slate-900/90 border border-slate-700/80 text-slate-200 text-xs font-medium flex items-center space-x-1.5 shadow-xs">
                        <Icon className="w-3.5 h-3.5 text-cyan-400" />
                        <span>{item.label}</span>
                      </span>
                    );
                  })}
                </div>
              </div>
            </div>

            {/* Quick Metrics Cards */}
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
              <div 
                onClick={() => setActiveTab('roadmap')}
                className="bg-slate-900/80 border border-slate-800 hover:border-cyan-500/50 rounded-2xl p-5 space-y-2 transition-all cursor-pointer shadow-lg group"
              >
                <div className="flex items-center justify-between text-slate-400">
                  <span className="text-xs font-bold uppercase tracking-wider">Build Roadmap</span>
                  <ListOrdered className="w-5 h-5 text-cyan-400 group-hover:scale-110 transition-transform" />
                </div>
                <div className="text-2xl font-extrabold text-white font-mono">18 Phases</div>
                <p className="text-xs text-slate-400">From Manifest setup to final APK compilation</p>
              </div>

              <div 
                onClick={() => setActiveTab('tools_matrix')}
                className="bg-slate-900/80 border border-slate-800 hover:border-amber-500/50 rounded-2xl p-5 space-y-2 transition-all cursor-pointer shadow-lg group"
              >
                <div className="flex items-center justify-between text-slate-400">
                  <span className="text-xs font-bold uppercase tracking-wider">Function Tools</span>
                  <Wrench className="w-5 h-5 text-amber-400 group-hover:scale-110 transition-transform" />
                </div>
                <div className="text-2xl font-extrabold text-white font-mono">21 Tools</div>
                <p className="text-xs text-slate-400">WhatsApp, Browser, File System, System toggles</p>
              </div>

              <div 
                onClick={() => setActiveTab('permissions')}
                className="bg-slate-900/80 border border-slate-800 hover:border-emerald-500/50 rounded-2xl p-5 space-y-2 transition-all cursor-pointer shadow-lg group"
              >
                <div className="flex items-center justify-between text-slate-400">
                  <span className="text-xs font-bold uppercase tracking-wider">Permissions Wizard</span>
                  <ShieldCheck className="w-5 h-5 text-emerald-400 group-hover:scale-110 transition-transform" />
                </div>
                <div className="text-2xl font-extrabold text-white font-mono">12 Permissions</div>
                <p className="text-xs text-slate-400">With Vivo Funtouch OS Autostart shortcuts</p>
              </div>

              <div 
                onClick={() => setActiveTab('vivo_diagnostics')}
                className="bg-slate-900/80 border border-slate-800 hover:border-indigo-500/50 rounded-2xl p-5 space-y-2 transition-all cursor-pointer shadow-lg group"
              >
                <div className="flex items-center justify-between text-slate-400">
                  <span className="text-xs font-bold uppercase tracking-wider">Vivo Y15s Specs</span>
                  <Smartphone className="w-5 h-5 text-indigo-400 group-hover:scale-110 transition-transform" />
                </div>
                <div className="text-2xl font-extrabold text-white font-mono">3GB RAM</div>
                <p className="text-xs text-slate-400">Node recycling & low RAM footprint</p>
              </div>
            </div>

            {/* Architectural Layer Stack Diagram */}
            <div className="bg-slate-900 border border-slate-800 rounded-3xl p-6 sm:p-8 space-y-6 shadow-xl">
              <div className="flex items-center justify-between border-b border-slate-800 pb-4">
                <div>
                  <h2 className="text-lg font-bold text-white flex items-center space-x-2">
                    <Activity className="w-5 h-5 text-cyan-400" />
                    <span>Cipher 5-Layer Master System Architecture</span>
                  </h2>
                  <p className="text-xs text-slate-400 mt-0.5">End-to-end data flow from hardware microphone to Gemini AI decision and phone action execution</p>
                </div>
              </div>

              <div className="grid grid-cols-1 md:grid-cols-5 gap-4">
                {[
                  {
                    layer: 'Layer 1',
                    title: 'Always On Layer',
                    tech: 'Foreground Service + Sherpa-ONNX',
                    desc: '24/7 background service, offline KWS for "Get Ready Cipher", zero battery standby drain.',
                    color: 'border-cyan-500/40 bg-cyan-950/20 text-cyan-300'
                  },
                  {
                    layer: 'Layer 2',
                    title: 'Intelligence Layer',
                    tech: 'Gemini Live API + Offline Fallback',
                    desc: 'Bilingual voice, function calling decisions, instant local intent execution without internet.',
                    color: 'border-amber-500/40 bg-amber-950/20 text-amber-300'
                  },
                  {
                    layer: 'Layer 3',
                    title: 'Control Layer',
                    tech: 'AccessibilityService + NotificationListener',
                    desc: 'Screen node tree reader (eyes) and gesture clicker/typer (hands) in all apps.',
                    color: 'border-emerald-500/40 bg-emerald-950/20 text-emerald-300'
                  },
                  {
                    layer: 'Layer 4',
                    title: 'Action Layer',
                    tech: 'WhatsApp + Browser + System Controllers',
                    desc: 'Direct app controllers for WhatsApp messaging, browser navigation, and file system.',
                    color: 'border-indigo-500/40 bg-indigo-950/20 text-indigo-300'
                  },
                  {
                    layer: 'Layer 5',
                    title: 'UI Layer',
                    tech: 'Jetpack Compose + Floating Orb HUD',
                    desc: 'Permission wizard, settings dashboard, and SYSTEM_ALERT_WINDOW floating HUD.',
                    color: 'border-purple-500/40 bg-purple-950/20 text-purple-300'
                  }
                ].map((item, idx) => (
                  <div key={idx} className={`p-4 rounded-2xl border ${item.color} space-y-2 flex flex-col justify-between`}>
                    <div>
                      <span className="text-[10px] font-mono font-bold uppercase opacity-80">{item.layer}</span>
                      <h3 className="text-sm font-bold text-white mt-0.5">{item.title}</h3>
                      <p className="text-[11px] font-semibold mt-1 opacity-90">{item.tech}</p>
                      <p className="text-[11px] text-slate-400 mt-2 leading-relaxed">{item.desc}</p>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>
        )}

        {/* TAB CONTENTS */}
        {activeTab === 'permissions' && <PermissionWizard />}
        {activeTab === 'voice_simulator' && (
          <LiveVoiceSimulator 
            onTriggerToolCall={handleToolTriggeredFromSim} 
            isOrbVisible={isOrbVisible}
            setIsOrbVisible={setIsOrbVisible}
          />
        )}
        {activeTab === 'accessibility' && <AccessibilityInspector />}
        {activeTab === 'tools_matrix' && <FunctionToolsMatrix />}
        {activeTab === 'vivo_diagnostics' && <VivoDiagnostics />}
        {activeTab === 'roadmap' && <RoadmapView />}
      </main>
    </div>
  );
}
