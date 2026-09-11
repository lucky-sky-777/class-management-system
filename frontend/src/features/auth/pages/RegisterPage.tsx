import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuthInternal } from '@features/auth/hooks/useAuthInternal';
import { User, AtSign, Lock, Eye, EyeOff, Mail, Image, ArrowLeft } from 'lucide-react';

export const RegisterPage = () => {
    const [step, setStep] = useState(1);
    const [formData, setFormData] = useState({
        username: '',
        displayName: '',
        password: '',
        confirmPassword: '',
        email: '',
        avatarUrl: ''
    });
    const [showPassword, setShowPassword] = useState(false);
    const [showConfirmPassword, setShowConfirmPassword] = useState(false);
    const [localError, setLocalError] = useState<string | null>(null);
    const { signup, isLoading, error } = useAuthInternal();
    const navigate = useNavigate();

    const handleNextStep = (e: React.FormEvent) => {
        e.preventDefault();
        setLocalError(null);
        if (!formData.username.trim()) {
            setLocalError('Vui lòng nhập Tên đăng nhập');
            return;
        }
        if (!formData.password) {
            setLocalError('Vui lòng nhập Mật khẩu');
            return;
        }
        if (formData.password !== formData.confirmPassword) {
            setLocalError('Mật khẩu nhập lại không khớp');
            return;
        }
        setStep(2);
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setLocalError(null);
        if (!formData.displayName.trim() || !formData.email.trim()) {
            setLocalError('Vui lòng nhập đầy đủ thông tin bắt buộc');
            return;
        }
        
        const success = await signup(formData.username, formData.password, formData.displayName, formData.email, formData.avatarUrl);
        if (success) {
            navigate('/login');
        }
    };

    return (
        <div className="min-h-screen bg-paper flex items-center justify-center p-6">
            <div className="card w-full max-w-[440px] shadow-sm animate-fade-up">
                <div className="card-header flex-col items-center py-10 border-none relative">
                    {step === 2 && (
                        <button 
                            type="button" 
                            onClick={() => setStep(1)} 
                            className="absolute left-6 top-10 text-ink-3 hover:text-ink-1 transition-colors"
                            aria-label="Quay lại"
                        >
                            <ArrowLeft size={20} />
                        </button>
                    )}
                    <h1 className="text-3xl font-serif font-semibold text-ink-1 tracking-tightest">
                        {step === 1 ? 'Tạo tài khoản' : 'Thông tin hồ sơ'}
                    </h1>
                    <p className="text-sm text-ink-3 mt-2 font-sans text-center">
                        {step === 1 
                            ? 'Bắt đầu hành trình giáo dục hiện đại cùng Class Management' 
                            : 'Hoàn tất thông tin cá nhân của bạn'}
                    </p>
                </div>

                {(error || localError) && (
                    <div className="px-8 pb-4">
                        <div className="p-3 bg-ink-red-fill border border-ink-red-border rounded text-ink-red-text text-xs font-medium animate-pulse-dot">
                            {error || localError}
                        </div>
                    </div>
                )}

                {step === 1 ? (
                    <form onSubmit={handleNextStep} className="card-body space-y-5 px-8 pb-10 pt-0">
                        <div className="input-wrap flex flex-col gap-1.5">
                            <label className="input-label">Tên đăng nhập</label>
                            <div className="input-field">
                                <AtSign size={16} className="text-ink-3" />
                                <input
                                    type="text"
                                    placeholder="Ví dụ: nva_2024"
                                    value={formData.username}
                                    onChange={(e) => setFormData({ ...formData, username: e.target.value })}
                                    required
                                    className="focus:outline-none w-full bg-transparent text-ink-1"
                                />
                            </div>
                        </div>

                        <div className="input-wrap flex flex-col gap-1.5">
                            <label className="input-label">Mật khẩu</label>
                            <div className="input-field">
                                <Lock size={16} className="text-ink-3" />
                                <input
                                    type={showPassword ? 'text' : 'password'}
                                    placeholder="zxhb102_"
                                    value={formData.password}
                                    onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                                    required
                                    className="focus:outline-none w-full bg-transparent text-ink-1"
                                />
                                <button
                                    type="button"
                                    onClick={() => setShowPassword(!showPassword)}
                                    className="text-ink-3 hover:text-ink-1 focus:outline-none ml-2 flex items-center justify-center"
                                    tabIndex={-1}
                                >
                                    {showPassword ? <EyeOff size={16} /> : <Eye size={16} />}
                                </button>
                            </div>
                        </div>

                        <div className="input-wrap flex flex-col gap-1.5">
                            <label className="input-label">Nhập lại mật khẩu</label>
                            <div className="input-field">
                                <Lock size={16} className="text-ink-3" />
                                <input
                                    type={showConfirmPassword ? 'text' : 'password'}
                                    placeholder="••••••••"
                                    value={formData.confirmPassword}
                                    onChange={(e) => setFormData({ ...formData, confirmPassword: e.target.value })}
                                    required
                                    className="focus:outline-none w-full bg-transparent text-ink-1"
                                />
                                <button
                                    type="button"
                                    onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                                    className="text-ink-3 hover:text-ink-1 focus:outline-none ml-2 flex items-center justify-center"
                                    tabIndex={-1}
                                >
                                    {showConfirmPassword ? <EyeOff size={16} /> : <Eye size={16} />}
                                </button>
                            </div>
                        </div>

                        <button
                            type="submit"
                            className="btn btn-primary w-full py-2.5 font-semibold transition-base mt-2"
                        >
                            Tiếp tục
                        </button>

                        <div className="text-center pt-6 border-t border-rule">
                            <p className="text-sm text-ink-2">
                                Đã có tài khoản?{' '}
                                <Link to="/login" className="text-warm-400 font-semibold hover:text-warm-600 transition-colors">
                                    Đăng nhập
                                </Link>
                            </p>
                        </div>
                    </form>
                ) : (
                    <form onSubmit={handleSubmit} className="card-body space-y-5 px-8 pb-10 pt-0">
                        <div className="input-wrap flex flex-col gap-1.5">
                            <label className="input-label">Họ và tên</label>
                            <div className="input-field">
                                <User size={16} className="text-ink-3" />
                                <input
                                    type="text"
                                    placeholder="Ví dụ: Nguyễn Văn An"
                                    value={formData.displayName}
                                    onChange={(e) => setFormData({ ...formData, displayName: e.target.value })}
                                    required
                                    className="focus:outline-none w-full bg-transparent text-ink-1"
                                />
                            </div>
                        </div>

                        <div className="input-wrap flex flex-col gap-1.5">
                            <label className="input-label">Email</label>
                            <div className="input-field">
                                <Mail size={16} className="text-ink-3" />
                                <input
                                    type="email"
                                    placeholder="Ví dụ: example@domain.com"
                                    value={formData.email}
                                    onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                                    required
                                    className="focus:outline-none w-full bg-transparent text-ink-1"
                                />
                            </div>
                        </div>

                        <div className="input-wrap flex flex-col gap-1.5">
                            <label className="input-label">URL ảnh đại diện (Tùy chọn)</label>
                            <div className="input-field">
                                <Image size={16} className="text-ink-3" />
                                <input
                                    type="url"
                                    placeholder="https://example.com/avatar.jpg"
                                    value={formData.avatarUrl}
                                    onChange={(e) => setFormData({ ...formData, avatarUrl: e.target.value })}
                                    className="focus:outline-none w-full bg-transparent text-ink-1"
                                />
                            </div>
                        </div>

                        <button
                            type="submit"
                            disabled={isLoading}
                            className={`btn btn-primary w-full py-2.5 font-semibold transition-base mt-2 ${isLoading ? 'opacity-70 cursor-not-allowed' : ''}`}
                        >
                            {isLoading ? 'Đang xử lý...' : 'Hoàn tất đăng ký'}
                        </button>
                    </form>
                )}
            </div>
        </div>
    );
};
