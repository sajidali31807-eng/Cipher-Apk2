import React, { useState } from 'react';
import { MOCK_WHATSAPP_NODE_TREE } from '../data/cipherData';
import { AccessibilityNode } from '../types';
import { 
  Eye, 
  Smartphone, 
  Search, 
  Layers, 
  MousePointer, 
  Code2, 
  Sparkles,
  CheckCircle2,
  Maximize2
} from 'lucide-react';

export const AccessibilityInspector: React.FC = () => {
  const [selectedApp, setSelectedApp] = useState<'whatsapp' | 'settings' | 'chrome'>('whatsapp');
  const [searchQuery, setSearchQuery] = useState('com.whatsapp:id/send');
  const [activeLayer, setActiveLayer] = useState<'tree' | 'vision'>('tree');
  const [selectedNode, setSelectedNode] = useState<AccessibilityNode | null>(null);

  return (
    <div className="space-y-6">
      {/* Top Banner */}
      <div className="bg-gradient-to-r from-slate-900 via-indigo-950 to-slate-900 border border-slate-800 rounded-2xl p-6 relative overflow-hidden shadow-xl">
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div>
            <div className="flex items-center space-x-2 text-cyan-400 text-xs font-semibold uppercase tracking-wider mb-1">
              <Eye className="w-4 h-4" />
              <span>Feature #4 & #6 • Accessibility Service (Eyes & Hands)</span>
            </div>
            <h2 className="text-2xl font-bold text-white tracking-tight">Accessibility Tree & Vision Inspector</h2>
            <p className="text-slate-300 text-sm mt-1 max-w-2xl">
              Cipher inspects every screen element in real time. Layer 1 uses AccessibilityNodeInfo for instant click/type injection; Layer 2 uses Gemini Vision screenshot analysis as a fallback.
            </p>
          </div>

          <div className="flex items-center space-x-2 bg-slate-900/80 p-1.5 rounded-xl border border-slate-800">
            <button
              onClick={() => setActiveLayer('tree')}
              className={`px-3 py-1.5 rounded-lg text-xs font-semibold transition-all cursor-pointer ${
                activeLayer === 'tree'
                  ? 'bg-cyan-500 text-slate-950 shadow-md'
                  : 'text-slate-400 hover:text-white'
              }`}
            >
              Layer 1: Node Tree
            </button>
            <button
              onClick={() => setActiveLayer('vision')}
              className={`px-3 py-1.5 rounded-lg text-xs font-semibold transition-all cursor-pointer ${
                activeLayer === 'vision'
                  ? 'bg-amber-500 text-slate-950 shadow-md'
                  : 'text-slate-400 hover:text-white'
              }`}
            >
              Layer 2: Gemini Vision
            </button>
          </div>
        </div>
      </div>

      {/* App Selector Bar */}
      <div className="flex items-center justify-between bg-slate-900 p-3 rounded-xl border border-slate-800">
        <div className="flex items-center space-x-2">
          <span className="text-xs font-semibold text-slate-400">Mock App Screen:</span>
          {(['whatsapp', 'settings', 'chrome'] as const).map((app) => (
            <button
              key={app}
              onClick={() => setSelectedApp(app)}
              className={`px-3 py-1.5 rounded-lg text-xs font-medium capitalize transition-colors cursor-pointer ${
                selectedApp === app
                  ? 'bg-slate-800 text-cyan-400 border border-slate-700 font-bold'
                  : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              {app}
            </button>
          ))}
        </div>

        <div className="flex items-center space-x-2">
          <Search className="w-4 h-4 text-slate-400" />
          <input
            type="text"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            placeholder="Search Resource ID or Text..."
            className="bg-slate-950 border border-slate-800 rounded-lg px-3 py-1 text-xs text-white placeholder-slate-500 focus:outline-none focus:border-cyan-500"
          />
        </div>
      </div>

      {/* Main Grid: Device Screen Simulation + Node Inspector */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
        {/* Device Frame Simulation */}
        <div className="lg:col-span-5 flex justify-center">
          <div className="w-[320px] h-[580px] bg-slate-950 border-4 border-slate-800 rounded-[36px] p-3 shadow-2xl relative flex flex-col justify-between overflow-hidden">
            {/* Phone Notch */}
            <div className="w-28 h-4 bg-slate-800 rounded-b-xl mx-auto mb-2 flex items-center justify-center">
              <div className="w-3 h-3 rounded-full bg-slate-900 border border-slate-700"></div>
            </div>

            {/* Screen Content Simulation */}
            <div className="flex-1 bg-slate-900 rounded-2xl p-3 flex flex-col justify-between relative overflow-hidden border border-slate-800">
              {/* WhatsApp Mock UI */}
              {selectedApp === 'whatsapp' && (
                <div className="flex flex-col h-full justify-between space-y-2 text-xs">
                  {/* WhatsApp Header */}
                  <div className="bg-emerald-800 p-2.5 rounded-xl text-white flex items-center justify-between">
                    <span className="font-bold">Rahul Sharma</span>
                    <span className="text-[10px] bg-emerald-900 px-2 py-0.5 rounded font-mono">ONLINE</span>
                  </div>

                  {/* Chat Bubbles */}
                  <div className="flex-1 space-y-2 overflow-y-auto py-2">
                    <div 
                      onClick={() => setSelectedNode({
                        id: 'msg_1',
                        text: 'Hey, are you free for the meeting at 4 PM?',
                        className: 'android.widget.TextView',
                        resourceId: 'com.whatsapp:id/message_text',
                        clickable: false,
                        bounds: { x: 10, y: 80, width: 280, height: 40 }
                      })}
                      className="bg-slate-800 p-2.5 rounded-xl border border-slate-700 text-slate-200 cursor-pointer hover:border-cyan-500 transition-colors"
                    >
                      <p className="text-[11px]">Hey, are you free for the meeting at 4 PM?</p>
                      <span className="text-[9px] text-slate-500 block text-right mt-1">16:02</span>
                    </div>

                    <div className="bg-emerald-950 p-2.5 rounded-xl border border-emerald-800 text-emerald-200 ml-auto max-w-[80%] font-mono">
                      <p className="text-[11px]">Yes, I will send the files shortly.</p>
                      <span className="text-[9px] text-emerald-400 block text-right mt-1">16:04</span>
                    </div>
                  </div>

                  {/* WhatsApp Message Entry & Send Button */}
                  <div className="bg-slate-950 p-2 rounded-xl border border-slate-800 flex items-center space-x-2">
                    <div className="flex-1 bg-slate-900 px-2.5 py-1.5 rounded-lg text-slate-400 font-mono text-[10px] truncate">
                      com.whatsapp:id/entry
                    </div>
                    <button 
                      onClick={() => setSelectedNode({
                        id: 'send_btn',
                        text: 'Send',
                        className: 'android.widget.ImageButton',
                        resourceId: 'com.whatsapp:id/send',
                        contentDescription: 'Send',
                        clickable: true,
                        bounds: { x: 300, y: 650, width: 50, height: 50 }
                      })}
                      className="p-2 rounded-lg bg-emerald-500 text-slate-950 font-bold text-[10px] cursor-pointer hover:bg-emerald-400"
                    >
                      SEND
                    </button>
                  </div>
                </div>
              )}

              {/* Settings Mock UI */}
              {selectedApp === 'settings' && (
                <div className="space-y-2 text-xs">
                  <div className="font-bold text-white text-sm mb-3">Settings • Vivo Y15s</div>
                  {['WiFi & Network', 'Bluetooth', 'Accessibility', 'Battery & Power', 'Autostart Manager'].map((s, i) => (
                    <div key={i} className="bg-slate-800/80 p-2.5 rounded-xl border border-slate-700 text-slate-200 flex justify-between items-center">
                      <span>{s}</span>
                      <span className="text-[10px] text-slate-500 font-mono">click()</span>
                    </div>
                  ))}
                </div>
              )}

              {/* Chrome Mock UI */}
              {selectedApp === 'chrome' && (
                <div className="space-y-2 text-xs">
                  <div className="bg-slate-950 p-2 rounded-xl border border-slate-800 font-mono text-[10px] text-cyan-400 truncate">
                    https://www.google.com/search?q=vivo+y15s
                  </div>
                  <div className="bg-slate-800 p-3 rounded-xl border border-slate-700 text-slate-200 space-y-1">
                    <div className="font-bold text-cyan-400">Google Search Result Node</div>
                    <p className="text-[10px] text-slate-400">Vivo Y15s Specifications: 3GB RAM, Android 12, Funtouch OS 12.</p>
                  </div>
                </div>
              )}

              {/* Vision Heatmap Overlay */}
              {activeLayer === 'vision' && (
                <div className="absolute inset-0 bg-amber-500/10 border-2 border-dashed border-amber-400 rounded-2xl flex items-center justify-center p-4 backdrop-blur-2xs">
                  <div className="bg-slate-950/90 p-3 rounded-xl border border-amber-500/40 text-center space-y-1">
                    <Sparkles className="w-5 h-5 text-amber-400 mx-auto animate-spin" />
                    <p className="text-xs font-bold text-amber-300">Gemini Vision OCR Heatmap</p>
                    <p className="text-[10px] text-slate-400 font-mono">Bounding Box Coordinates: [x: 300, y: 650, w: 50, h: 50]</p>
                  </div>
                </div>
              )}
            </div>

            {/* Bottom Home Indicator */}
            <div className="w-24 h-1 bg-slate-700 rounded-full mx-auto mt-2"></div>
          </div>
        </div>

        {/* Tree Node Hierarchy Details */}
        <div className="lg:col-span-7 bg-slate-900 border border-slate-800 rounded-2xl p-5 space-y-4 shadow-xl">
          <div className="flex items-center justify-between border-b border-slate-800 pb-3">
            <h3 className="text-sm font-bold text-white flex items-center space-x-2">
              <Code2 className="w-4 h-4 text-cyan-400" />
              <span>AccessibilityNodeInfo Tree Parser</span>
            </h3>
            <span className="text-xs px-2 py-0.5 rounded bg-emerald-950 text-emerald-400 border border-emerald-800 font-mono">
              Fuzzy Match Status: MATCHED
            </span>
          </div>

          {/* Node Code Details */}
          <div className="bg-slate-950 p-4 rounded-xl border border-slate-800 font-mono text-xs text-slate-300 space-y-2 overflow-x-auto">
            <div className="text-slate-500">// Real-time Android AccessibilityNodeInfo Output</div>
            <div className="text-cyan-400 font-bold">
              Target Element: {selectedNode?.resourceId || 'com.whatsapp:id/send'}
            </div>
            <div>className: <span className="text-amber-300">{selectedNode?.className || 'android.widget.ImageButton'}</span></div>
            <div>text: <span className="text-emerald-300">"{selectedNode?.text || 'Send'}"</span></div>
            <div>contentDescription: <span className="text-emerald-300">"{selectedNode?.contentDescription || 'Send button'}"</span></div>
            <div>clickable: <span className="text-purple-400">{selectedNode?.clickable !== false ? 'true' : 'false'}</span></div>
            <div>boundsInScreen: <span className="text-blue-400">Rect(300, 650 - 350, 700)</span></div>
          </div>

          <div className="bg-slate-900/80 p-4 rounded-xl border border-slate-800 space-y-2">
            <h4 className="text-xs font-bold text-slate-200 flex items-center space-x-1.5">
              <CheckCircle2 className="w-4 h-4 text-cyan-400" />
              <span>Action Dispatcher Strategy (Multi-Stage)</span>
            </h4>
            <ul className="text-xs text-slate-400 space-y-1.5 pl-5 list-disc">
              <li><strong className="text-slate-200">Stage 1:</strong> Query node by exact Android resource ID (<code className="text-cyan-400">com.whatsapp:id/send</code>).</li>
              <li><strong className="text-slate-200">Stage 2:</strong> Fuzzy string matching on <code className="text-amber-300">text</code> or <code className="text-emerald-300">contentDescription</code>.</li>
              <li><strong className="text-slate-200">Stage 3:</strong> Fallback to Gemini Vision screenshot coordinate click via Accessibility gesture path injection.</li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  );
};
