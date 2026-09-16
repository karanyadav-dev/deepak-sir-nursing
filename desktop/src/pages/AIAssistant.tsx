import { useState, useRef, useEffect } from 'react';
import Layout from '../components/Layout';
import { Send, Bot, User, Copy, RotateCw, Trash2 } from 'lucide-react';
import { api } from '../services/api';

interface Message {
  id: string;
  role: 'user' | 'assistant';
  content: string;
  createdAt: string;
}

export default function AIAssistant() {
  const [messages, setMessages] = useState<Message[]>([
    {
      id: '1',
      role: 'assistant',
      content: '👋 Hello! I am your Nursing AI Assistant. Ask me anything about nursing topics, anatomy, pharmacology, care plans, or exam preparation.',
      createdAt: new Date().toISOString(),
    },
  ]);
  const [input, setInput] = useState('');
  const [loading, setLoading] = useState(false);
  const [chatId, setChatId] = useState<string | null>(null);
  const scrollRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    scrollRef.current?.scrollTo({ top: scrollRef.current.scrollHeight, behavior: 'smooth' });
  }, [messages]);

  const createChat = async () => {
    try {
      const response = await api.post('/ai-assistant/chats', {
        title: 'Teaching Session',
        mode: 'GENERAL',
      });
      if (response.data?.success) {
        setChatId(response.data.data.id);
        return response.data.data.id;
      }
    } catch (error) {
      console.error('Failed to create chat');
    }
    return null;
  };

  const sendMessage = async () => {
    if (!input.trim() || loading) return;

    const userMessage: Message = {
      id: Date.now().toString(),
      role: 'user',
      content: input,
      createdAt: new Date().toISOString(),
    };
    setMessages([...messages, userMessage]);
    setInput('');
    setLoading(true);

    try {
      let currentChatId = chatId;
      if (!currentChatId) {
        currentChatId = await createChat();
        if (!currentChatId) throw new Error('Failed to create chat');
      }

      // Send message via multipart
      const formData = new FormData();
      formData.append('content', input);
      formData.append('language', 'en');

      const response = await api.post(
        `/ai-assistant/chats/${currentChatId}/messages`,
        formData,
        { headers: { 'Content-Type': 'multipart/form-data' } }
      );

      if (response.data?.success) {
        const aiMessage: Message = {
          id: response.data.data.id,
          role: 'assistant',
          content: response.data.data.content,
          createdAt: response.data.data.createdAt,
        };
        setMessages(prev => [...prev, aiMessage]);
      } else {
        throw new Error('AI failed');
      }
    } catch (error: any) {
      const errorMessage: Message = {
        id: Date.now().toString(),
        role: 'assistant',
        content: '❌ Sorry, something went wrong. Please try again.',
        createdAt: new Date().toISOString(),
      };
      setMessages(prev => [...prev, errorMessage]);
    } finally {
      setLoading(false);
    }
  };

  const clearChat = () => {
    if (!window.confirm('Clear conversation?')) return;
    setMessages([{
      id: '1',
      role: 'assistant',
      content: '👋 Hello! I am your Nursing AI Assistant. Ask me anything!',
      createdAt: new Date().toISOString(),
    }]);
    setChatId(null);
  };

  const copyMessage = (content: string) => {
    navigator.clipboard.writeText(content);
    alert('✅ Copied!');
  };

  return (
    <Layout title="AI Assistant" showBack backTo="/classroom">
      <div className="h-full flex flex-col bg-gray-50">
        {/* Messages */}
        <div ref={scrollRef} className="flex-1 overflow-y-auto p-6">
          <div className="max-w-4xl mx-auto space-y-4">
            {messages.map((msg) => (
              <div
                key={msg.id}
                className={`flex gap-3 ${msg.role === 'user' ? 'justify-end' : 'justify-start'}`}
              >
                {msg.role === 'assistant' && (
                  <div className="w-8 h-8 rounded-full bg-blue-500 flex items-center justify-center flex-shrink-0">
                    <Bot size={18} className="text-white" />
                  </div>
                )}

                <div className={`max-w-2xl px-4 py-3 rounded-2xl ${
                  msg.role === 'user'
                    ? 'bg-blue-600 text-white rounded-br-sm'
                    : 'bg-white border border-gray-200 text-gray-800 rounded-bl-sm'
                }`}>
                  <div className="whitespace-pre-wrap text-sm">{msg.content}</div>

                  {msg.role === 'assistant' && (
                    <button
                      onClick={() => copyMessage(msg.content)}
                      className="mt-2 text-xs text-gray-500 hover:text-blue-600 flex items-center gap-1"
                    >
                      <Copy size={12} /> Copy
                    </button>
                  )}
                </div>

                {msg.role === 'user' && (
                  <div className="w-8 h-8 rounded-full bg-gray-700 flex items-center justify-center flex-shrink-0">
                    <User size={18} className="text-white" />
                  </div>
                )}
              </div>
            ))}

            {loading && (
              <div className="flex gap-3">
                <div className="w-8 h-8 rounded-full bg-blue-500 flex items-center justify-center">
                  <Bot size={18} className="text-white" />
                </div>
                <div className="bg-white border border-gray-200 px-4 py-3 rounded-2xl">
                  <div className="flex gap-1">
                    <div className="w-2 h-2 rounded-full bg-gray-400 animate-bounce" />
                    <div className="w-2 h-2 rounded-full bg-gray-400 animate-bounce" style={{ animationDelay: '0.1s' }} />
                    <div className="w-2 h-2 rounded-full bg-gray-400 animate-bounce" style={{ animationDelay: '0.2s' }} />
                  </div>
                </div>
              </div>
            )}
          </div>
        </div>

        {/* Input */}
        <div className="border-t border-gray-200 bg-white p-4">
          <div className="max-w-4xl mx-auto flex gap-2">
            <input
              type="text"
              value={input}
              onChange={(e) => setInput(e.target.value)}
              onKeyPress={(e) => e.key === 'Enter' && sendMessage()}
              placeholder="Ask anything about nursing..."
              className="flex-1 px-4 py-3 border border-gray-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500"
              disabled={loading}
            />
            <button
              onClick={clearChat}
              className="px-3 bg-gray-100 rounded-xl hover:bg-gray-200"
              title="Clear chat"
            >
              <Trash2 size={20} className="text-gray-600" />
            </button>
            <button
              onClick={sendMessage}
              disabled={loading || !input.trim()}
              className="bg-blue-600 text-white px-6 rounded-xl hover:bg-blue-700 disabled:opacity-50 flex items-center gap-2"
            >
              <Send size={18} />
              Send
            </button>
          </div>

          <div className="max-w-4xl mx-auto mt-2 flex gap-2 flex-wrap">
            {['Explain hypertension', 'Generate 5 MCQs', 'Make short notes on diabetes', 'Nursing care plan for fever'].map((quick) => (
              <button
                key={quick}
                onClick={() => setInput(quick)}
                className="text-xs px-3 py-1 bg-blue-50 text-blue-600 rounded-full hover:bg-blue-100"
              >
                {quick}
              </button>
            ))}
          </div>
        </div>
      </div>
    </Layout>
  );
}