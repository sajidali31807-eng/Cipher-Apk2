import React, { useState, useEffect, useRef } from 'react';
import { VoiceMessage } from '../types';
import { CIPHER_TOOLS } from '../data/cipherData';
import { 
  Mic, 
  Send, 
  Sparkles, 
  Volume2, 
  Bot, 
  User, 
  Radio, 
  CheckCircle2, 
  Globe, 
  Zap, 
  WifiOff,
  RotateCcw,
  Play
} from 'lucide-react';

interface LiveVoiceSimulatorProps {
  onTriggerToolCall?: (toolName: string, args: any) => void;
  isOrbVisible: boolean;
  setIsOrbVisible: (val: boolean) => void;
}

export const LiveVoiceSimulator: React.FC<LiveVoiceSimulatorProps> = ({
  onTriggerToolCall,
  isOrbVisible,
  setIsOrbVisible
}) => {
  const [messages, setMessages] = useState<VoiceMessage[]>([
    {
      id: 'm1',
      sender: 'system',
      text: 'Sherpa-ONNX listening offline in background for wake word "Get Ready Cipher"...',
      timestamp: '21:30:00'
    },
    {
      id: 'm2',
      sender: 'cipher',
      text: 'Hello! I am Cipher, your AI Assistant. Say "Get Ready Cipher" or type a command in English or Hindi.',
      language: 'Bilingual',
      timestamp: '21:30:02'
    }
  ]);

  const [inputCommand, setInputCommand] = useState('');
  const [isListening, setIsListening] = useState(false);
  const [isProcessing, setIsProcessing] = useState(false);
  const [isOfflineMode, setIsOfflineMode] = useState(false);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  const handleWakeWordTrigger = () => {
    setIsOrbVisible(true);
    const wakeMsg: VoiceMessage = {
      id: `m_${Date.now()}`,
      sender: 'system',
      text: '⚡ Sherpa-ONNX Keyword Detected: "GET READY CIPHER" (100% Offline KWS)',
      timestamp: new Date().toLocaleTimeString()
    };
    
    const replyMsg: VoiceMessage = {
      id: `m_${Date.now() + 1}`,
      sender: 'cipher',
      text: 'Haan bolo, main ready hoon! Kya karna hai phone par? (Yes, I am ready! What should I do on your phone?)',
      language: 'Bilingual',
      timestamp: new Date().toLocaleTimeString()
    };

    setMessages(prev => [...prev, wakeMsg, replyMsg]);
  };

  const handleSendMessage = (textToSend?: string) => {
    const text = textToSend || inputCommand.trim();
    if (!text) return;

    const userMsg: VoiceMessage = {
      id: `u_${Date.now()}`,
      sender: 'user',
      text: text,
      timestamp: new Date().toLocaleTimeString()
    };

    setMessages(prev => [...prev, userMsg]);
    if (!textToSend) setInputCommand('');
    setIsProcessing(true);

    // Simulate Gemini Live AI + Function Calling decision
    setTimeout(() => {
      let responseText = '';
      let toolCall: { name: string; args: any; result: any; status: 'executed' | 'fallback' } | undefined = undefined;

      const lower = text.toLowerCase();

      if (lower.includes('whatsapp') || lower.includes('message') || lower.includes('chat')) {
        toolCall = {
          name: 'send_whatsapp_message',
          args: { contact: 'Rahul Sharma', message: 'Main 10 min mein aa raha hoon' },
          result: 'WhatsApp opened -> Contact selected -> Message typed & sent successfully',
          status: 'executed'
        };
        responseText = 'Rahul Sharma ko WhatsApp message bhej diya: "Main 10 min mein aa raha hoon".';
      } else if (lower.includes('flashlight') || lower.includes('torch') || lower.includes('light')) {
        toolCall = {
          name: 'toggle_flashlight',
          args: { state: !lower.includes('off') },
          result: 'Hardware torch LED toggled',
          status: isOfflineMode ? 'fallback' : 'executed'
        };
        responseText = lower.includes('off') ? 'Flashlight band kar di hai.' : 'Flashlight on kar di hai!';
      } else if (lower.includes('battery') || lower.includes('charge')) {
        toolCall = {
          name: 'get_battery_level',
          args: {},
          result: 'Battery: 82% (Discharging, Normal)',
          status: isOfflineMode ? 'fallback' : 'executed'
        };
        responseText = 'Aapke phone ki battery 82% hai aur sab kuch sahi chal raha hai.';
      } else if (lower.includes('notification') || lower.includes('read')) {
        toolCall = {
          name: 'read_notifications',
          args: {},
          result: 'Found 2 recent unread notifications from WhatsApp and Gmail',
          status: 'executed'
        };
        responseText = 'Aapke paas WhatsApp par Rahul ka 1 unread message hai aur 1 new Gmail email hai.';
      } else if (lower.includes('youtube') || lower.includes('app') || lower.includes('open')) {
        const appName = lower.includes('youtube') ? 'YouTube' : lower.includes('chrome') ? 'Chrome' : 'Settings';
        toolCall = {
          name: 'open_app',
          args: { app_name: appName },
          result: `Launched ${appName} via PackageManager Intent`,
          status: 'executed'
        };
        responseText = `${appName} app khol diya hai!`;
      } else {
        responseText = 'Samajh gaya! Gemini Live dwara instruction process kar raha hoon.';
      }

      const botReply: VoiceMessage = {
        id: `c_${Date.now()}`,
        sender: 'cipher',
        text: responseText,
        language: 'Bilingual',
        timestamp: new Date().toLocaleTimeString(),
        toolCalls: toolCall ? [{ name: toolCall.name, args: toolCall.args, result: toolCall.result, status: toolCall.status }] : undefined
      };

      setMessages(prev => [...prev, botReply]);
      setIsProcessing(false);
      
      if (onTriggerToolCall && toolCall) {
        onTriggerToolCall(toolCall.name, toolCall.args);
      }
    }, 1200);
  };

  return (
    <div className="space-y-6">
      {/* Top Banner */}
      <div className="bg-gradient-to-r from-slate-900 via-slate-950 to-slate-900 border border-slate-800 rounded-2xl p-6 relative overflow-hidden shadow-xl">
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div>
            <div className="flex items-center space-x-2 text-cyan-400 text-xs font-semibold uppercase tracking-wider mb-1">
              <Zap className="w-4 h-4" />
              <span>Feature #2 & #3 • Gemini Live Voice Brain + Sherpa-ONNX</span>
            </div>
            <h2 className="text-2xl font-bold text-white tracking-tight">Live Voice & Function Calling Console</h2>
            <p className="text-slate-300 text-sm mt-1">
              Simulate voice dialogue in Hindi + English. The AI listens, decides tool calls, and responds naturally.
            </p>
          </div>

          <div className="flex items-center space-x-3">
            {/* Mode Toggle (Online vs Offline Fallback) */}
            <button
              onClick={() => setIsOfflineMode(!isOfflineMode)}
              className={`px-3.5 py-2 rounded-xl border text-xs font-semibold flex items-center space-x-2 transition-all cursor-pointer ${
                isOfflineMode
                  ? 'bg-amber-950/80 border-amber-700 text-amber-300'
                  : 'bg-slate-800 border-slate-700 text-emerald-400'
              }`}
            >
              {isOfflineMode ? <WifiOff className="w-4 h-4" /> : <Globe className="w-4 h-4" />}
              <span>{isOfflineMode ? 'Offline Fallback Active' : 'Gemini Live Online'}</span>
            </button>

            <button
              onClick={handleWakeWordTrigger}
              className="px-4 py-2 rounded-xl bg-gradient-to-r from-cyan-500 to-blue-600 text-slate-950 font-bold text-xs flex items-center space-x-2 shadow-lg shadow-cyan-950 hover:from-cyan-400 hover:to-blue-500 transition-all cursor-pointer"
            >
              <Mic className="w-4 h-4" />
              <span>Say "Get Ready Cipher"</span>
            </button>
          </div>
        </div>
      </div>

      {/* Suggested Quick Prompts */}
      <div className="flex items-center space-x-2 overflow-x-auto pb-2 scrollbar-none">
        <span className="text-xs font-medium text-slate-500 whitespace-nowrap">Try Commands:</span>
        {[
          "Send WhatsApp message to Rahul saying I am on my way",
          "Turn on flashlight",
          "Check battery level",
          "Open YouTube",
          "Read my unread notifications"
        ].map((prompt, idx) => (
          <button
            key={idx}
            onClick={() => handleSendMessage(prompt)}
            className="px-3 py-1.5 rounded-lg bg-slate-800/80 hover:bg-slate-700 text-slate-300 text-xs whitespace-nowrap transition-colors border border-slate-700/80 cursor-pointer"
          >
            "{prompt}"
          </button>
        ))}
      </div>

      {/* Conversation Thread */}
      <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-4 md:p-6 min-h-[420px] max-h-[500px] overflow-y-auto flex flex-col space-y-4 shadow-inner">
        {messages.map((msg) => {
          if (msg.sender === 'system') {
            return (
              <div key={msg.id} className="flex justify-center my-1">
                <div className="px-3.5 py-1.5 rounded-full bg-slate-800/80 border border-slate-700/60 text-[11px] font-mono text-cyan-300 flex items-center space-x-2 shadow-xs">
                  <Radio className="w-3 h-3 text-cyan-400 animate-pulse" />
                  <span>{msg.text}</span>
                  <span className="text-slate-500 text-[10px]">{msg.timestamp}</span>
                </div>
              </div>
            );
          }

          const isUser = msg.sender === 'user';
          return (
            <div
              key={msg.id}
              className={`flex items-start space-x-3 ${isUser ? 'flex-row-reverse space-x-reverse' : ''}`}
            >
              {/* Avatar */}
              <div
                className={`w-8 h-8 rounded-xl flex items-center justify-center flex-shrink-0 text-xs font-bold ${
                  isUser
                    ? 'bg-blue-600 text-white'
                    : 'bg-gradient-to-tr from-cyan-600 to-indigo-600 text-white shadow-md shadow-cyan-900/50'
                }`}
              >
                {isUser ? <User className="w-4 h-4" /> : <Bot className="w-4 h-4" />}
              </div>

              {/* Message Bubble */}
              <div className={`max-w-[85%] sm:max-w-[70%] space-y-2`}>
                <div
                  className={`p-4 rounded-2xl text-xs sm:text-sm leading-relaxed ${
                    isUser
                      ? 'bg-blue-600 text-white rounded-tr-xs'
                      : 'bg-slate-800/90 text-slate-100 border border-slate-700 rounded-tl-xs shadow-md'
                  }`}
                >
                  <p>{msg.text}</p>

                  {/* Tool Call Action Badge */}
                  {msg.toolCalls && msg.toolCalls.map((tc, i) => (
                    <div
                      key={i}
                      className="mt-3 p-3 rounded-xl bg-slate-950/80 border border-amber-500/30 text-xs font-mono space-y-1"
                    >
                      <div className="flex items-center justify-between text-amber-300 font-semibold">
                        <span className="flex items-center space-x-1">
                          <CheckCircle2 className="w-3.5 h-3.5" />
                          <span>Tool Execution: {tc.name}</span>
                        </span>
                        <span className="text-[10px] px-1.5 py-0.5 rounded bg-amber-950 text-amber-400 border border-amber-800">
                          {tc.status === 'fallback' ? 'OFFLINE FALLBACK' : 'GEMINI LIVE API'}
                        </span>
                      </div>
                      <div className="text-slate-400 text-[11px] font-mono">
                        Args: {JSON.stringify(tc.args)}
                      </div>
                      {tc.result && (
                        <div className="text-emerald-400 text-[11px] pt-1 border-t border-slate-800 font-sans">
                          Result: {tc.result}
                        </div>
                      )}
                    </div>
                  ))}
                </div>

                <div className={`flex items-center space-x-2 text-[10px] text-slate-500 ${isUser ? 'justify-end' : ''}`}>
                  <span>{msg.timestamp}</span>
                  {msg.language && (
                    <span className="px-1.5 py-0.5 rounded bg-slate-800 text-slate-400 border border-slate-700">
                      {msg.language}
                    </span>
                  )}
                </div>
              </div>
            </div>
          );
        })}

        {isProcessing && (
          <div className="flex items-center space-x-2 text-cyan-400 text-xs font-mono p-2">
            <Sparkles className="w-4 h-4 animate-spin" />
            <span>Gemini Live API evaluating intent & generating tool call response...</span>
          </div>
        )}
        <div ref={messagesEndRef} />
      </div>

      {/* Input Bar */}
      <div className="flex items-center space-x-2">
        <input
          type="text"
          value={inputCommand}
          onChange={(e) => setInputCommand(e.target.value)}
          onKeyDown={(e) => e.key === 'Enter' && handleSendMessage()}
          placeholder="Speak or type command (e.g. 'Turn on torch', 'WhatsApp Rahul', 'Call Mom')..."
          className="flex-1 bg-slate-900 border border-slate-800 focus:border-cyan-500 rounded-xl px-4 py-3 text-sm text-white placeholder-slate-500 focus:outline-none transition-colors"
        />
        <button
          onClick={() => handleSendMessage()}
          disabled={!inputCommand.trim() || isProcessing}
          className="px-5 py-3 rounded-xl bg-cyan-500 hover:bg-cyan-400 disabled:opacity-50 text-slate-950 font-bold text-xs flex items-center space-x-2 transition-all cursor-pointer shadow-lg shadow-cyan-950"
        >
          <span>Send</span>
          <Send className="w-4 h-4" />
        </button>
      </div>
    </div>
  );
};
