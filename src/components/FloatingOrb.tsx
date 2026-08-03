import React, { useState } from 'react';
import { Mic, Volume2, X, Sparkles, CheckCircle2, Bot } from 'lucide-react';

interface FloatingOrbProps {
  isVisible: boolean;
  onClose: () => void;
  statusText: string;
  isListening: boolean;
  isThinking: boolean;
  isSpeaking: boolean;
  currentToolCall?: string;
}

export const FloatingOrb: React.FC<FloatingOrbProps> = ({
  isVisible,
  onClose,
  statusText,
  isListening,
  isThinking,
  isSpeaking,
  currentToolCall
}) => {
  const [position, setPosition] = useState({ x: 20, y: 100 });
  const [isDragging, setIsDragging] = useState(false);

  if (!isVisible) return null;

  return (
    <div 
      className="fixed z-50 transition-all duration-300"
      style={{ right: `${position.x}px`, top: `${position.y}px` }}
    >
      <div className="relative group">
        {/* Glow Effects */}
        <div className={`absolute -inset-2 rounded-full blur-xl opacity-75 animate-pulse transition-colors ${
          isThinking 
            ? 'bg-amber-500' 
            : isSpeaking 
            ? 'bg-emerald-500' 
            : 'bg-cyan-500'
        }`} />

        {/* Floating Orb Main Core Container */}
        <div className="relative flex items-center space-x-3 bg-slate-900/90 backdrop-blur-md border border-cyan-500/40 p-2.5 rounded-full shadow-2xl shadow-cyan-950/80 cursor-grab active:cursor-grabbing">
          {/* Pulsating Center Orb Sphere */}
          <div className="relative w-11 h-11 rounded-full bg-gradient-to-tr from-cyan-600 via-blue-600 to-indigo-600 flex items-center justify-center p-0.5 shadow-inner">
            <div className="w-full h-full rounded-full bg-slate-950/40 backdrop-blur-xs flex items-center justify-center relative overflow-hidden">
              {/* Particle Waves */}
              {(isListening || isSpeaking) && (
                <div className="absolute inset-0 flex items-center justify-center space-x-0.5 opacity-80">
                  <span className="w-1 bg-cyan-400 rounded-full animate-bounce h-3" style={{ animationDelay: '0ms' }}></span>
                  <span className="w-1 bg-blue-400 rounded-full animate-bounce h-5" style={{ animationDelay: '150ms' }}></span>
                  <span className="w-1 bg-cyan-300 rounded-full animate-bounce h-4" style={{ animationDelay: '300ms' }}></span>
                  <span className="w-1 bg-indigo-400 rounded-full animate-bounce h-2" style={{ animationDelay: '450ms' }}></span>
                </div>
              )}
              {isThinking && (
                <Sparkles className="w-5 h-5 text-amber-300 animate-spin" />
              )}
              {!isListening && !isThinking && !isSpeaking && (
                <Bot className="w-5 h-5 text-cyan-300" />
              )}
            </div>
          </div>

          {/* Status Label & Active Tool Call Display */}
          <div className="pr-3 pl-1 max-w-[200px]">
            <div className="flex items-center space-x-1.5">
              <span className={`w-2 h-2 rounded-full ${
                isThinking 
                  ? 'bg-amber-400 animate-ping' 
                  : isSpeaking 
                  ? 'bg-emerald-400 animate-pulse' 
                  : 'bg-cyan-400 animate-pulse'
              }`} />
              <p className="text-xs font-semibold text-white truncate font-mono">
                {isThinking ? 'Thinking...' : isSpeaking ? 'Cipher Speaking' : isListening ? 'Listening...' : 'Ready'}
              </p>
            </div>
            <p className="text-[10px] text-slate-300 truncate font-mono mt-0.5">
              {currentToolCall ? (
                <span className="text-amber-300 flex items-center space-x-1">
                  <CheckCircle2 className="w-2.5 h-2.5" />
                  <span>{currentToolCall}</span>
                </span>
              ) : (
                statusText || '"Get Ready Cipher"'
              )}
            </p>
          </div>

          {/* Close HUD Button */}
          <button 
            onClick={onClose}
            className="p-1 rounded-full text-slate-400 hover:text-white hover:bg-slate-800 transition-colors cursor-pointer"
            title="Hide Floating HUD"
          >
            <X className="w-4 h-4" />
          </button>
        </div>
      </div>
    </div>
  );
};
