export type TabType = 
  | 'overview' 
  | 'permissions' 
  | 'voice_simulator' 
  | 'accessibility' 
  | 'tools_matrix' 
  | 'vivo_diagnostics' 
  | 'roadmap';

export interface PhaseInfo {
  id: number;
  title: string;
  description: string;
  status: 'pending' | 'ready' | 'in_progress' | 'completed';
  category: 'Core' | 'Voice & AI' | 'Accessibility' | 'Automation' | 'System & Vivo';
  techStack: string[];
  keyDeliverables: string[];
  codeSnippet?: string;
}

export interface ToolFunction {
  name: string;
  description: string;
  parameters: {
    [key: string]: {
      type: string;
      description: string;
      required?: boolean;
    };
  };
  category: 'App Control' | 'Messaging' | 'Browser' | 'System' | 'Screen';
  isOfflineSupported?: boolean;
}

export interface PermissionItem {
  id: string;
  name: string;
  androidPermission: string;
  description: string;
  isCritical: boolean;
  granted: boolean;
  intentAction?: string;
  vivoSpecific?: boolean;
}

export interface AccessibilityNode {
  id: string;
  text?: string;
  className: string;
  resourceId?: string;
  contentDescription?: string;
  clickable: boolean;
  bounds: { x: number; y: number; width: number; height: number };
  children?: AccessibilityNode[];
}

export interface VoiceMessage {
  id: string;
  sender: 'user' | 'cipher' | 'system';
  text: string;
  language?: 'English' | 'Hindi' | 'Bilingual';
  timestamp: string;
  toolCalls?: Array<{
    name: string;
    args: Record<string, any>;
    result?: any;
    status: 'calling' | 'executed' | 'fallback';
  }>;
}
