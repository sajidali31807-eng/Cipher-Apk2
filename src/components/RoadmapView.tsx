import React, { useState } from 'react';
import { CIPHER_PHASES } from '../data/cipherData';
import { PhaseInfo } from '../types';
import { 
  ListOrdered, 
  CheckCircle2, 
  Code2, 
  Terminal, 
  ChevronDown, 
  ChevronRight,
  Sparkles,
  Layers
} from 'lucide-react';

export const RoadmapView: React.FC = () => {
  const [selectedPhase, setSelectedPhase] = useState<PhaseInfo>(CIPHER_PHASES[0]);
  const [filterCategory, setFilterCategory] = useState<string>('All');

  const categories = ['All', 'Core', 'Voice & AI', 'Accessibility', 'Automation', 'System & Vivo'];

  const filteredPhases = CIPHER_PHASES.filter(
    p => filterCategory === 'All' || p.category === filterCategory
  );

  return (
    <div className="space-y-6">
      {/* Top Banner */}
      <div className="bg-gradient-to-r from-slate-900 via-slate-950 to-slate-900 border border-slate-800 rounded-2xl p-6 relative overflow-hidden shadow-xl">
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div>
            <div className="flex items-center space-x-2 text-cyan-400 text-xs font-semibold uppercase tracking-wider mb-1">
              <ListOrdered className="w-4 h-4" />
              <span>Implementation Roadmap • 18 Phases Total</span>
            </div>
            <h2 className="text-2xl font-bold text-white tracking-tight">Project Cipher Android Build Roadmap</h2>
            <p className="text-slate-300 text-sm mt-1 max-w-2xl">
              Complete phase-by-phase execution architecture from initial AndroidManifest configuration to standalone APK compilation.
            </p>
          </div>

          <div className="bg-slate-900/90 p-3 rounded-xl border border-slate-800 font-mono text-xs text-slate-300 flex items-center space-x-3">
            <span className="text-emerald-400 font-bold">18 Phases Ready</span>
            <span className="text-slate-600">|</span>
            <span className="text-cyan-400">Kotlin + Compose</span>
          </div>
        </div>
      </div>

      {/* Category Filter */}
      <div className="flex items-center space-x-2 overflow-x-auto pb-2 scrollbar-none">
        {categories.map(cat => (
          <button
            key={cat}
            onClick={() => setFilterCategory(cat)}
            className={`px-3.5 py-1.5 rounded-lg text-xs font-medium transition-colors cursor-pointer whitespace-nowrap ${
              filterCategory === cat
                ? 'bg-cyan-500 text-slate-950 font-bold shadow-md'
                : 'text-slate-400 hover:text-white bg-slate-900 border border-slate-800'
            }`}
          >
            {cat}
          </button>
        ))}
      </div>

      {/* Main Grid: Phase List + Selected Phase Inspector */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
        {/* Phase List Column */}
        <div className="lg:col-span-5 space-y-3 max-h-[600px] overflow-y-auto pr-1">
          {filteredPhases.map((phase) => {
            const isSelected = selectedPhase.id === phase.id;
            return (
              <div
                key={phase.id}
                onClick={() => setSelectedPhase(phase)}
                className={`p-4 rounded-2xl border transition-all cursor-pointer ${
                  isSelected
                    ? 'bg-slate-900 border-cyan-500 ring-1 ring-cyan-500/30 shadow-lg'
                    : 'bg-slate-900/60 border-slate-800/80 hover:border-slate-700'
                }`}
              >
                <div className="flex items-center justify-between">
                  <span className="text-xs font-mono font-bold text-cyan-400">Phase {phase.id}</span>
                  <span className="text-[10px] px-2 py-0.5 rounded bg-slate-800 text-slate-400 border border-slate-700 font-mono">
                    {phase.category}
                  </span>
                </div>
                <h3 className="text-sm font-bold text-white mt-1">{phase.title}</h3>
                <p className="text-xs text-slate-400 mt-1 line-clamp-2 leading-relaxed">
                  {phase.description}
                </p>
              </div>
            );
          })}
        </div>

        {/* Selected Phase Detail View */}
        <div className="lg:col-span-7 bg-slate-900 border border-slate-800 rounded-2xl p-6 space-y-5 shadow-xl">
          <div className="border-b border-slate-800 pb-4">
            <div className="flex items-center space-x-2 text-xs font-mono text-cyan-400 font-bold">
              <span>PHASE {selectedPhase.id} OF 18</span>
              <span>•</span>
              <span className="text-emerald-400">{selectedPhase.category}</span>
            </div>
            <h2 className="text-xl font-bold text-white mt-1">{selectedPhase.title}</h2>
            <p className="text-xs text-slate-300 mt-2 leading-relaxed">{selectedPhase.description}</p>
          </div>

          {/* Key Deliverables */}
          <div className="space-y-2">
            <h4 className="text-xs font-bold text-slate-200 uppercase tracking-wider flex items-center space-x-1.5">
              <CheckCircle2 className="w-4 h-4 text-cyan-400" />
              <span>Key Technical Deliverables</span>
            </h4>
            <ul className="space-y-2 text-xs text-slate-300">
              {selectedPhase.keyDeliverables.map((deliv, idx) => (
                <li key={idx} className="flex items-start space-x-2 bg-slate-950 p-2.5 rounded-xl border border-slate-800">
                  <span className="text-cyan-400 font-bold">•</span>
                  <span>{deliv}</span>
                </li>
              ))}
            </ul>
          </div>

          {/* Tech Stack */}
          <div className="space-y-2">
            <h4 className="text-xs font-bold text-slate-200 uppercase tracking-wider">Tech Stack & APIs</h4>
            <div className="flex flex-wrap gap-2">
              {selectedPhase.techStack.map((tech, idx) => (
                <span key={idx} className="px-2.5 py-1 rounded-lg bg-slate-800 text-slate-300 text-xs font-mono border border-slate-700">
                  {tech}
                </span>
              ))}
            </div>
          </div>

          {/* Code Snippet if present */}
          {selectedPhase.codeSnippet && (
            <div className="space-y-2">
              <h4 className="text-xs font-bold text-slate-200 uppercase tracking-wider flex items-center space-x-1.5">
                <Code2 className="w-4 h-4 text-amber-400" />
                <span>Reference Kotlin / Android Code Architecture</span>
              </h4>
              <pre className="bg-slate-950 p-4 rounded-xl border border-slate-800 text-[11px] font-mono text-slate-300 overflow-x-auto leading-relaxed">
                <code>{selectedPhase.codeSnippet}</code>
              </pre>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
