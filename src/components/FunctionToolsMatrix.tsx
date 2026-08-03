import React, { useState } from 'react';
import { CIPHER_TOOLS } from '../data/cipherData';
import { ToolFunction } from '../types';
import { 
  Wrench, 
  Play, 
  CheckCircle2, 
  WifiOff, 
  Search, 
  Zap,
  Layers,
  Code
} from 'lucide-react';

export const FunctionToolsMatrix: React.FC = () => {
  const [selectedCategory, setSelectedCategory] = useState<string>('All');
  const [searchQuery, setSearchQuery] = useState<string>('');
  const [lastExecutedResult, setLastExecutedResult] = useState<{ tool: string; result: string } | null>(null);

  const categories = ['All', 'App Control', 'Messaging', 'Browser', 'System', 'Screen'];

  const filteredTools = CIPHER_TOOLS.filter(tool => {
    const matchesCategory = selectedCategory === 'All' || tool.category === selectedCategory;
    const matchesSearch = tool.name.toLowerCase().includes(searchQuery.toLowerCase()) || 
                          tool.description.toLowerCase().includes(searchQuery.toLowerCase());
    return matchesCategory && matchesSearch;
  });

  const handleTestTool = (tool: ToolFunction) => {
    let mockResult = '';
    if (tool.name === 'open_app') mockResult = 'App launched via PackageManager Intent';
    else if (tool.name === 'send_whatsapp_message') mockResult = 'Message sent to contact in WhatsApp';
    else if (tool.name === 'toggle_flashlight') mockResult = 'Camera torch LED hardware toggled';
    else if (tool.name === 'get_battery_level') mockResult = 'Battery: 82% (Discharging)';
    else mockResult = `Executed tool call ${tool.name}() successfully`;

    setLastExecutedResult({ tool: tool.name, result: mockResult });
  };

  return (
    <div className="space-y-6">
      {/* Top Banner */}
      <div className="bg-gradient-to-r from-slate-900 via-slate-950 to-slate-900 border border-slate-800 rounded-2xl p-6 relative overflow-hidden shadow-xl">
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div>
            <div className="flex items-center space-x-2 text-cyan-400 text-xs font-semibold uppercase tracking-wider mb-1">
              <Wrench className="w-4 h-4" />
              <span>Feature #5 • Function Calling System</span>
            </div>
            <h2 className="text-2xl font-bold text-white tracking-tight">21 Function Calling Tools Sandbox</h2>
            <p className="text-slate-300 text-sm mt-1 max-w-2xl">
              All decisions are made by Gemini AI through structured function calling. 14 tools also support offline execution via local intents.
            </p>
          </div>

          <div className="bg-slate-900/90 p-3 rounded-xl border border-slate-800 font-mono text-xs text-slate-300 flex items-center space-x-3">
            <span className="text-cyan-400 font-bold">{CIPHER_TOOLS.length} Defined Tools</span>
            <span className="text-slate-600">|</span>
            <span className="text-emerald-400">14 Offline Support</span>
          </div>
        </div>
      </div>

      {/* Execution Result Banner */}
      {lastExecutedResult && (
        <div className="bg-emerald-950/80 border border-emerald-500/40 rounded-xl p-4 flex items-center justify-between text-emerald-200 text-xs font-mono shadow-md">
          <div className="flex items-center space-x-2">
            <CheckCircle2 className="w-4 h-4 text-emerald-400" />
            <span>Simulated Execution [{lastExecutedResult.tool}()]: {lastExecutedResult.result}</span>
          </div>
          <button 
            onClick={() => setLastExecutedResult(null)}
            className="text-emerald-400 hover:text-white cursor-pointer font-bold"
          >
            Dismiss
          </button>
        </div>
      )}

      {/* Search & Category Filter */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 bg-slate-900 p-4 rounded-xl border border-slate-800">
        <div className="flex items-center space-x-2 overflow-x-auto pb-1 sm:pb-0 scrollbar-none">
          {categories.map(cat => (
            <button
              key={cat}
              onClick={() => setSelectedCategory(cat)}
              className={`px-3 py-1.5 rounded-lg text-xs font-medium whitespace-nowrap transition-colors cursor-pointer ${
                selectedCategory === cat
                  ? 'bg-cyan-500 text-slate-950 font-bold shadow-md'
                  : 'text-slate-400 hover:text-white bg-slate-800'
              }`}
            >
              {cat}
            </button>
          ))}
        </div>

        <div className="flex items-center space-x-2">
          <Search className="w-4 h-4 text-slate-400" />
          <input
            type="text"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            placeholder="Filter tools..."
            className="bg-slate-950 border border-slate-800 rounded-lg px-3 py-1.5 text-xs text-white placeholder-slate-500 focus:outline-none focus:border-cyan-500 w-full sm:w-48"
          />
        </div>
      </div>

      {/* Tools Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {filteredTools.map((tool) => (
          <div
            key={tool.name}
            className="bg-slate-900/80 border border-slate-800 rounded-2xl p-5 hover:border-slate-700 transition-all flex flex-col justify-between space-y-4 shadow-lg"
          >
            <div className="space-y-2">
              <div className="flex items-center justify-between">
                <span className="text-xs font-mono font-bold text-cyan-400">{tool.name}()</span>
                <div className="flex items-center space-x-1">
                  {tool.isOfflineSupported && (
                    <span className="text-[10px] px-2 py-0.5 rounded bg-emerald-950 text-emerald-400 border border-emerald-800 font-mono font-semibold" title="Supports Offline Intent Execution">
                      OFFLINE
                    </span>
                  )}
                  <span className="text-[10px] px-2 py-0.5 rounded bg-slate-800 text-slate-400 border border-slate-700 font-mono">
                    {tool.category}
                  </span>
                </div>
              </div>

              <p className="text-xs text-slate-300 leading-relaxed">
                {tool.description}
              </p>

              {/* Parameters JSON */}
              <div className="bg-slate-950 p-2.5 rounded-xl border border-slate-800 text-[11px] font-mono text-slate-400">
                <div className="text-slate-500 text-[10px] uppercase font-bold mb-1">Parameters Schema:</div>
                {Object.keys(tool.parameters).length > 0 ? (
                  Object.entries(tool.parameters).map(([key, param]) => (
                    <div key={key} className="text-slate-300">
                      • <span className="text-amber-300">{key}</span>: <span className="text-purple-400">{param.type}</span> ({param.description})
                    </div>
                  ))
                ) : (
                  <span className="text-slate-600 font-italic">No required parameters</span>
                )}
              </div>
            </div>

            <button
              onClick={() => handleTestTool(tool)}
              className="w-full py-2 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-200 text-xs font-semibold flex items-center justify-center space-x-2 transition-colors cursor-pointer border border-slate-700"
            >
              <Play className="w-3.5 h-3.5 text-cyan-400" />
              <span>Simulate Tool Execution</span>
            </button>
          </div>
        ))}
      </div>
    </div>
  );
};
