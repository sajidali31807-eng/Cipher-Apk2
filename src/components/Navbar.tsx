import React from 'react';
import { TabType } from '../types';
import { 
  ShieldCheck, 
  Mic, 
  Eye, 
  Wrench, 
  Smartphone, 
  ListOrdered, 
  Activity,
  Zap,
  Radio
} from 'lucide-react';

interface NavbarProps {
  activeTab: TabType;
  setActiveTab: (tab: TabType) => void;
  isListening: boolean;
  onTriggerWakeWord: () => void;
  isOrbVisible: boolean;
  setIsOrbVisible: (val: boolean) => void;
}

export const Navbar: React.FC<NavbarProps> = ({
  activeTab,
  setActiveTab,
  isListening,
  onTriggerWakeWord,
  isOrbVisible,
  setIsOrbVisible
}) => {
  return (
    <header className="bg-slate-900 border-b border-slate-800 sticky top-0 z-40 shadow-xl">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          {/* Logo & Brand Identity */}
          <div className="flex items-center space-x-3">
            <div className="relative">
              <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-cyan-600 via-blue-600 to-indigo-600 flex items-center justify-center shadow-lg shadow-cyan-500/20 ring-1 ring-cyan-400/30">
                <Radio className="w-5 h-5 text-white animate-pulse" />
              </div>
              {isListening && (
                <span className="absolute -top-1 -right-1 flex h-3 w-3">
                  <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-cyan-400 opacity-75"></span>
                  <span className="relative inline-flex rounded-full h-3 w-3 bg-cyan-500"></span>
                </span>
              )}
            </div>
            <div>
              <div className="flex items-center space-x-2">
                <h1 className="text-xl font-bold tracking-tight text-white font-mono">
                  CIPHER <span className="text-cyan-400 text-xs font-sans px-2 py-0.5 rounded-md bg-cyan-950/80 border border-cyan-800">V2.0 ARCHITECTURE</span>
                </h1>
              </div>
              <p className="text-xs text-slate-400">Jarvis AI Assistant • Vivo Y15s (Funtouch OS) • Android 12</p>
            </div>
          </div>

          {/* Device & Engine Badges */}
          <div className="hidden md:flex items-center space-x-3 text-xs">
            <div className="px-3 py-1.5 rounded-lg bg-slate-800/90 border border-slate-700/80 text-slate-300 flex items-center space-x-2">
              <span className="w-2 h-2 rounded-full bg-emerald-400 animate-pulse"></span>
              <span className="font-semibold text-emerald-400">Sherpa-ONNX</span>
              <span className="text-slate-500">|</span>
              <span>100% Offline KWS</span>
            </div>

            <div className="px-3 py-1.5 rounded-lg bg-slate-800/90 border border-slate-700/80 text-slate-300 flex items-center space-x-2">
              <Zap className="w-3.5 h-3.5 text-amber-400" />
              <span className="font-semibold text-amber-300">Gemini Live</span>
              <span className="text-slate-500">|</span>
              <span>Hinglish Voice</span>
            </div>

            {/* Quick Wake Word Trigger Button */}
            <button
              onClick={onTriggerWakeWord}
              className="px-3.5 py-1.5 rounded-lg bg-gradient-to-r from-cyan-500 to-blue-600 hover:from-cyan-400 hover:to-blue-500 text-white font-medium shadow-md shadow-cyan-900/40 flex items-center space-x-2 transition-all active:scale-95 cursor-pointer"
              title="Simulate saying 'Get Ready Cipher'"
            >
              <Mic className="w-4 h-4 animate-bounce" />
              <span>"Get Ready Cipher"</span>
            </button>

            {/* HUD Orb Toggle */}
            <button
              onClick={() => setIsOrbVisible(!isOrbVisible)}
              className={`px-3 py-1.5 rounded-lg border text-xs font-medium flex items-center space-x-1.5 transition-colors cursor-pointer ${
                isOrbVisible
                  ? 'bg-cyan-950/60 border-cyan-700 text-cyan-300'
                  : 'bg-slate-800 border-slate-700 text-slate-400 hover:text-slate-200'
              }`}
            >
              <Activity className="w-3.5 h-3.5" />
              <span>{isOrbVisible ? 'HUD Orb Active' : 'Toggle HUD Orb'}</span>
            </button>
          </div>
        </div>

        {/* Tab Bar */}
        <div className="flex space-x-1 overflow-x-auto py-2 border-t border-slate-800/80 scrollbar-none">
          {[
            { id: 'overview', label: 'Master Overview', icon: Activity },
            { id: 'permissions', label: 'Permission Wizard', icon: ShieldCheck },
            { id: 'voice_simulator', label: 'Live Voice & AI Brain', icon: Mic },
            { id: 'accessibility', label: 'Accessibility Inspector', icon: Eye },
            { id: 'tools_matrix', label: '21 Function Tools', icon: Wrench },
            { id: 'vivo_diagnostics', label: 'Vivo Y15s Tuning', icon: Smartphone },
            { id: 'roadmap', label: '18-Phase Implementation', icon: ListOrdered }
          ].map((tab) => {
            const Icon = tab.icon;
            const isActive = activeTab === tab.id;
            return (
              <button
                key={tab.id}
                onClick={() => setActiveTab(tab.id as TabType)}
                className={`flex items-center space-x-2 px-3.5 py-2 rounded-lg text-xs font-medium whitespace-nowrap transition-all cursor-pointer ${
                  isActive
                    ? 'bg-cyan-500/10 text-cyan-400 border border-cyan-500/30 font-semibold'
                    : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/50'
                }`}
              >
                <Icon className={`w-4 h-4 ${isActive ? 'text-cyan-400' : 'text-slate-500'}`} />
                <span>{tab.label}</span>
              </button>
            );
          })}
        </div>
      </div>
    </header>
  );
};
