import React, { useState } from 'react';
import { CIPHER_PERMISSIONS } from '../data/cipherData';
import { PermissionItem } from '../types';
import { 
  ShieldCheck, 
  ShieldAlert, 
  ExternalLink, 
  Check, 
  Smartphone, 
  Sparkles,
  Info,
  ArrowRight
} from 'lucide-react';

export const PermissionWizard: React.FC = () => {
  const [permissions, setPermissions] = useState<PermissionItem[]>(CIPHER_PERMISSIONS);
  const [activeFilter, setActiveFilter] = useState<'all' | 'critical' | 'vivo'>('all');

  const togglePermission = (id: string) => {
    setPermissions(prev =>
      prev.map(p => (p.id === id ? { ...p, granted: !p.granted } : p))
    );
  };

  const grantAllCritical = () => {
    setPermissions(prev =>
      prev.map(p => (p.isCritical ? { ...p, granted: true } : p))
    );
  };

  const filteredPermissions = permissions.filter(p => {
    if (activeFilter === 'critical') return p.isCritical;
    if (activeFilter === 'vivo') return p.vivoSpecific;
    return true;
  });

  const grantedCount = permissions.filter(p => p.granted).length;
  const criticalGranted = permissions.filter(p => p.isCritical && p.granted).length;
  const totalCritical = permissions.filter(p => p.isCritical).length;

  return (
    <div className="space-y-6">
      {/* Header Banner */}
      <div className="bg-gradient-to-r from-slate-900 via-indigo-950 to-slate-900 border border-slate-800 rounded-2xl p-6 relative overflow-hidden shadow-xl">
        <div className="absolute top-0 right-0 w-96 h-96 bg-cyan-500/10 rounded-full blur-3xl -mr-20 -mt-20 pointer-events-none" />
        
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 relative z-10">
          <div>
            <div className="flex items-center space-x-2 text-cyan-400 text-xs font-semibold uppercase tracking-wider mb-1">
              <ShieldCheck className="w-4 h-4" />
              <span>Feature #14 • Jetpack Compose Setup Wizard</span>
            </div>
            <h2 className="text-2xl font-bold text-white tracking-tight">Permission Onboarding Wizard</h2>
            <p className="text-slate-300 text-sm mt-1 max-w-2xl">
              Cipher requires elevated Android permissions to serve as your autonomous phone controller. 
              Each permission opens direct system intent settings.
            </p>
          </div>

          <div className="flex flex-col sm:flex-row gap-3">
            <button
              onClick={grantAllCritical}
              className="px-4 py-2.5 rounded-xl bg-cyan-500 hover:bg-cyan-400 text-slate-950 font-semibold text-xs flex items-center justify-center space-x-2 transition-all shadow-lg shadow-cyan-950 cursor-pointer"
            >
              <Sparkles className="w-4 h-4" />
              <span>Grant All Critical (Simulate)</span>
            </button>
          </div>
        </div>

        {/* Setup Progress Bar */}
        <div className="mt-6 pt-6 border-t border-slate-800/80 grid grid-cols-1 md:grid-cols-3 gap-4">
          <div className="bg-slate-900/80 p-4 rounded-xl border border-slate-800">
            <div className="text-xs text-slate-400 font-medium">Critical Permissions Status</div>
            <div className="text-xl font-bold text-white mt-1 font-mono flex items-center space-x-2">
              <span className={criticalGranted === totalCritical ? 'text-emerald-400' : 'text-amber-400'}>
                {criticalGranted} / {totalCritical}
              </span>
              <span className="text-xs font-normal text-slate-500 font-sans">
                ({Math.round((criticalGranted / totalCritical) * 100)}%)
              </span>
            </div>
          </div>

          <div className="bg-slate-900/80 p-4 rounded-xl border border-slate-800">
            <div className="text-xs text-slate-400 font-medium">Total Permissions Granted</div>
            <div className="text-xl font-bold text-white mt-1 font-mono flex items-center space-x-2">
              <span className="text-cyan-400">{grantedCount} / {permissions.length}</span>
            </div>
          </div>

          <div className="bg-slate-900/80 p-4 rounded-xl border border-slate-800 flex items-center space-x-3">
            <div className="w-10 h-10 rounded-lg bg-indigo-900/50 border border-indigo-700/50 flex items-center justify-center flex-shrink-0 text-indigo-300">
              <Smartphone className="w-5 h-5" />
            </div>
            <div>
              <div className="text-xs font-semibold text-indigo-300">Target Device</div>
              <div className="text-xs text-slate-300 font-medium mt-0.5">Vivo Y15s • Funtouch OS 12</div>
            </div>
          </div>
        </div>
      </div>

      {/* Filter Tabs */}
      <div className="flex items-center space-x-2 border-b border-slate-800 pb-3">
        {[
          { id: 'all', label: `All Permissions (${permissions.length})` },
          { id: 'critical', label: `Critical Only (${totalCritical})` },
          { id: 'vivo', label: 'Vivo Funtouch OS Specific' }
        ].map(filter => (
          <button
            key={filter.id}
            onClick={() => setActiveFilter(filter.id as any)}
            className={`px-3.5 py-1.5 rounded-lg text-xs font-medium transition-colors cursor-pointer ${
              activeFilter === filter.id
                ? 'bg-slate-800 text-cyan-400 border border-slate-700'
                : 'text-slate-400 hover:text-slate-200'
            }`}
          >
            {filter.label}
          </button>
        ))}
      </div>

      {/* Permission Cards Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {filteredPermissions.map(item => (
          <div
            key={item.id}
            className={`p-5 rounded-2xl border transition-all ${
              item.granted
                ? 'bg-slate-900/60 border-emerald-500/30 ring-1 ring-emerald-500/20'
                : item.isCritical
                ? 'bg-slate-900/90 border-amber-500/30 ring-1 ring-amber-500/10'
                : 'bg-slate-900/40 border-slate-800'
            }`}
          >
            <div className="flex items-start justify-between gap-3">
              <div className="flex items-start space-x-3">
                <div
                  className={`w-9 h-9 rounded-xl flex items-center justify-center flex-shrink-0 mt-0.5 ${
                    item.granted
                      ? 'bg-emerald-950 text-emerald-400 border border-emerald-800'
                      : item.isCritical
                      ? 'bg-amber-950 text-amber-400 border border-amber-800'
                      : 'bg-slate-800 text-slate-400 border border-slate-700'
                  }`}
                >
                  {item.granted ? (
                    <Check className="w-5 h-5" />
                  ) : item.isCritical ? (
                    <ShieldAlert className="w-5 h-5" />
                  ) : (
                    <ShieldCheck className="w-5 h-5" />
                  )}
                </div>

                <div>
                  <div className="flex items-center space-x-2">
                    <h3 className="text-sm font-bold text-white">{item.name}</h3>
                    {item.vivoSpecific && (
                      <span className="text-[10px] px-2 py-0.5 rounded bg-indigo-950 text-indigo-300 border border-indigo-800 font-mono font-semibold">
                        Vivo Y15s
                      </span>
                    )}
                    {item.isCritical && (
                      <span className="text-[10px] px-2 py-0.5 rounded bg-amber-950 text-amber-300 border border-amber-800 font-mono">
                        REQUIRED
                      </span>
                    )}
                  </div>
                  <p className="text-xs text-slate-400 mt-1 leading-relaxed">
                    {item.description}
                  </p>
                  <p className="text-[10px] text-slate-500 font-mono mt-2 truncate">
                    {item.androidPermission}
                  </p>
                </div>
              </div>
            </div>

            <div className="mt-4 pt-3 border-t border-slate-800/80 flex items-center justify-between">
              <span className="text-xs font-mono text-slate-400 flex items-center space-x-1.5">
                <span className={`w-2 h-2 rounded-full ${item.granted ? 'bg-emerald-400' : 'bg-slate-600'}`} />
                <span>{item.granted ? 'Granted & Active' : 'Not Granted'}</span>
              </span>

              <div className="flex items-center space-x-2">
                {item.intentAction && (
                  <button
                    onClick={() => {
                      alert(`[Simulated Intent] Opening System Settings:\n${item.intentAction}`);
                    }}
                    className="px-2.5 py-1 rounded-lg bg-slate-800 hover:bg-slate-700 text-slate-300 text-xs font-medium flex items-center space-x-1 transition-colors cursor-pointer border border-slate-700"
                    title="Simulate opening Android System Settings"
                  >
                    <span>System Settings</span>
                    <ExternalLink className="w-3 h-3 text-slate-400" />
                  </button>
                )}

                <button
                  onClick={() => togglePermission(item.id)}
                  className={`px-3 py-1 rounded-lg text-xs font-semibold transition-all cursor-pointer ${
                    item.granted
                      ? 'bg-slate-800 text-slate-400 hover:bg-slate-700'
                      : 'bg-cyan-500 text-slate-950 hover:bg-cyan-400'
                  }`}
                >
                  {item.granted ? 'Revoke' : 'Allow Permission'}
                </button>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
