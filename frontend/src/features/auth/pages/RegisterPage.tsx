import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuthInternal } from '@features/auth/hooks/useAuthInternal';
import { 
    User, 
    Lock, 
    Eye, 
    EyeOff, 
    ArrowLeft, 
    GraduationCap, 
    Phone, 
    Calendar, 
    Cake, 
    Plus, 
    Minus 
} from 'lucide-react';

const INITIAL_INTERESTS = [
    'Khoa học và Công nghệ',
    'Y tế và Sức khỏe',
    'Giáo dục',
    'Thiết kế',
    'Chính trị',
    'Phát luật',
    'Kinh tế',
    'Xây dựng'
];

const MORE_INTERESTS = [
    'Nghệ thuật & Âm nhạc',
    'Truyền thông & Marketing',
    'Môi trường & Sinh thái',
    'Tâm lý & Xã hội'
];

const INITIAL_HOBBIES = [
    'Code',
    'Vẽ',
    'Thiết kế',
    'Xem phim',
    'Chụp ảnh'
];

const MORE_HOBBIES = [
    'Đọc sách',
    'Chơi game',
    'Nghe nhạc',
    'Du lịch',
    'Viết lách'
];

export const RegisterPage: React.FC = () => {
    const [step, setStep] = useState<1 | 2 | 3>(1);
    const [formData, setFormData] = useState({
        displayName: '',
        username: '',
        password: '',
        confirmPassword: '',
        phoneNumber: '',
        dateOfBirth: '',
        gender: '' as 'Nam' | 'Nữ' | 'Khác' | '',
        major: '',
        interests: [] as string[],
        hobbies: [] as string[]
    });

    const [showPassword, setShowPassword] = useState(false);
    const [showConfirmPassword, setShowConfirmPassword] = useState(false);
    const [localError, setLocalError] = useState<string | null>(null);
    const [showAllInterests, setShowAllInterests] = useState(false);
    const [showAllHobbies, setShowAllHobbies] = useState(false);

    const { isLoading, error } = useAuthInternal();
    const navigate = useNavigate();

    const handleNextFromStep1 = (e: React.FormEvent) => {
        e.preventDefault();
        setLocalError(null);

        if (!formData.displayName.trim()) {
            setLocalError('Vui lòng nhập Họ và tên');
            return;
        }
        if (!formData.username.trim()) {
            setLocalError('Vui lòng nhập Tên đăng nhập');
            return;
        }
        if (!formData.password) {
            setLocalError('Vui lòng nhập Mật khẩu');
            return;
        }
        if (formData.password.length < 6) {
            setLocalError('Mật khẩu phải có ít nhất 6 ký tự');
            return;
        }
        if (formData.password !== formData.confirmPassword) {
            setLocalError('Mật khẩu xác nhận không khớp');
            return;
        }

        setStep(2);
    };

    const handleNextFromStep2 = (e: React.FormEvent) => {
        e.preventDefault();
        setLocalError(null);

        if (!formData.phoneNumber.trim()) {
            setLocalError('Vui lòng nhập Số điện thoại');
            return;
        }
        if (!formData.dateOfBirth.trim()) {
            setLocalError('Vui lòng nhập Ngày sinh');
            return;
        }
        if (!formData.gender) {
            setLocalError('Vui lòng chọn Giới tính');
            return;
        }

        setStep(3);
    };

    const handleCompleteStep3 = (e: React.FormEvent) => {
        e.preventDefault();
        setLocalError(null);

        // UI only - simulate success and navigate to login
        console.log('Registration Data Submitted:', formData);
        navigate('/login');
    };

    const toggleInterest = (item: string) => {
        setFormData(prev => ({
            ...prev,
            interests: prev.interests.includes(item)
                ? prev.interests.filter(i => i !== item)
                : [...prev.interests, item]
        }));
    };

    const toggleHobby = (item: string) => {
        setFormData(prev => ({
            ...prev,
            hobbies: prev.hobbies.includes(item)
                ? prev.hobbies.filter(h => h !== item)
                : [...prev.hobbies, item]
        }));
    };

    const displayedInterests = showAllInterests 
        ? [...INITIAL_INTERESTS, ...MORE_INTERESTS] 
        : INITIAL_INTERESTS;

    const displayedHobbies = showAllHobbies 
        ? [...INITIAL_HOBBIES, ...MORE_HOBBIES] 
        : INITIAL_HOBBIES;

    return (
        <div className="min-h-screen bg-[#f3f4f6] dark:bg-paper-dark flex flex-col items-center justify-center p-4 sm:p-6">
            {/* Top Stepper Bar */}
            <div className="w-full max-w-[460px] mb-6 sm:mb-8">
                <div className="flex items-center justify-between relative px-6 sm:px-8">
                    {/* Connecting Line 1 -> 2 */}
                    <div 
                        className={`absolute top-4 left-[20%] right-[50%] h-[2px] -translate-y-1/2 transition-colors duration-300 ${
                            step >= 2 ? 'bg-[#00a8e8]' : 'bg-gray-300 dark:bg-slate-600'
                        }`}
                    />
                    {/* Connecting Line 2 -> 3 */}
                    <div 
                        className={`absolute top-4 left-[50%] right-[20%] h-[2px] -translate-y-1/2 transition-colors duration-300 ${
                            step >= 3 ? 'bg-[#00a8e8]' : 'bg-gray-300 dark:bg-slate-600'
                        }`}
                    />

                    {/* Step 1 Indicator */}
                    <div className="flex flex-col items-center relative z-10">
                        <div 
                            className={`w-8 h-8 rounded-full flex items-center justify-center text-sm font-semibold transition-all duration-300 bg-white dark:bg-slate-800 ${
                                step >= 1 
                                    ? 'border-2 border-[#00a8e8] text-[#00a8e8]' 
                                    : 'border-2 border-gray-300 dark:border-slate-600 text-gray-400'
                            }`}
                        >
                            1
                        </div>
                        <span 
                            className={`text-xs mt-1.5 font-medium ${
                                step >= 1 ? 'text-[#00a8e8]' : 'text-gray-400'
                            }`}
                        >
                            Bước 1
                        </span>
                    </div>

                    {/* Step 2 Indicator */}
                    <div className="flex flex-col items-center relative z-10">
                        <div 
                            className={`w-8 h-8 rounded-full flex items-center justify-center text-sm font-semibold transition-all duration-300 bg-white dark:bg-slate-800 ${
                                step >= 2 
                                    ? 'border-2 border-[#00a8e8] text-[#00a8e8]' 
                                    : 'border-2 border-gray-300 dark:border-slate-600 text-gray-400'
                            }`}
                        >
                            2
                        </div>
                        <span 
                            className={`text-xs mt-1.5 font-medium ${
                                step >= 2 ? 'text-[#00a8e8]' : 'text-gray-400'
                            }`}
                        >
                            Bước 2
                        </span>
                    </div>

                    {/* Step 3 Indicator */}
                    <div className="flex flex-col items-center relative z-10">
                        <div 
                            className={`w-8 h-8 rounded-full flex items-center justify-center text-sm font-semibold transition-all duration-300 bg-white dark:bg-slate-800 ${
                                step >= 3 
                                    ? 'border-2 border-[#00a8e8] text-[#00a8e8]' 
                                    : 'border-2 border-gray-300 dark:border-slate-600 text-gray-400'
                            }`}
                        >
                            3
                        </div>
                        <span 
                            className={`text-xs mt-1.5 font-medium ${
                                step >= 3 ? 'text-[#00a8e8]' : 'text-gray-400'
                            }`}
                        >
                            Bước 3
                        </span>
                    </div>
                </div>
            </div>

            {/* Main Form Card */}
            <div className="card w-full max-w-[460px] bg-white dark:bg-surface-dark-1 shadow-sm border border-gray-200 dark:border-rule-dark rounded-2xl animate-fade-up relative overflow-hidden">
                {/* Card Header */}
                <div className="card-header flex-col items-center pt-8 pb-4 px-8 border-none relative">
                    {/* Back Button for Step 2 and Step 3 */}
                    {step > 1 && (
                        <button
                            type="button"
                            onClick={() => {
                                setLocalError(null);
                                setStep((prev) => (prev - 1) as 1 | 2);
                            }}
                            className="absolute left-6 top-7 text-ink-3 hover:text-ink-1 transition-colors p-1 rounded-lg hover:bg-surface-2 dark:hover:bg-surface-dark-2"
                            aria-label="Quay lại"
                        >
                            <ArrowLeft size={22} />
                        </button>
                    )}

                    {/* Graduation Cap Logo */}
                    <div className="w-12 h-12 rounded-xl border-2 border-[#00a8e8] flex items-center justify-center text-[#00a8e8] mb-3.5">
                        <GraduationCap size={28} />
                    </div>

                    {/* Titles based on current step */}
                    {step === 1 && (
                        <>
                            <h1 className="text-2xl font-bold text-ink-1 tracking-tight">
                                Tạo tài khoản
                            </h1>
                            <p className="text-xs text-ink-3 mt-1.5 text-center">
                                Bắt đầu hành trình cùng StudySpace
                            </p>
                        </>
                    )}

                    {step === 2 && (
                        <>
                            <h1 className="text-2xl font-bold text-ink-1 tracking-tight">
                                Thông tin cơ bản
                            </h1>
                            <p className="text-xs text-ink-3 mt-1.5 text-center">
                                Giúp StudySpace hiểu hơn về bạn
                            </p>
                        </>
                    )}

                    {step === 3 && (
                        <>
                            <h1 className="text-2xl font-bold text-ink-1 tracking-tight">
                                Hồ sơ cá nhân hóa
                            </h1>
                            <p className="text-xs text-ink-3 mt-1.5 text-center">
                                Dùng để gợi ý tài liệu và các nhóm học phù hợp
                            </p>
                        </>
                    )}
                </div>

                {/* Error Banner */}
                {(error || localError) && (
                    <div className="px-8 pb-2">
                        <div className="p-3 bg-ink-red-fill border border-ink-red-border rounded-lg text-ink-red-text text-xs font-medium animate-pulse-dot">
                            {error || localError}
                        </div>
                    </div>
                )}

                {/* ── STEP 1: ACCOUNT CREATION ── */}
                {step === 1 && (
                    <form onSubmit={handleNextFromStep1} className="card-body space-y-4 px-8 pb-8 pt-2">
                        <div className="input-wrap flex flex-col gap-1.5">
                            <label className="input-label">HỌ VÀ TÊN</label>
                            <div className="input-field">
                                <User size={16} className="text-ink-3 shrink-0" />
                                <input
                                    type="text"
                                    placeholder="Ví dụ: Nguyễn Văn A"
                                    value={formData.displayName}
                                    onChange={(e) => setFormData({ ...formData, displayName: e.target.value })}
                                    required
                                    className="focus:outline-none w-full bg-transparent text-ink-1"
                                />
                            </div>
                        </div>

                        <div className="input-wrap flex flex-col gap-1.5">
                            <label className="input-label">TÊN ĐĂNG NHẬP</label>
                            <div className="input-field">
                                <User size={16} className="text-ink-3 shrink-0" />
                                <input
                                    type="text"
                                    placeholder="Ví dụ: nva_2026"
                                    value={formData.username}
                                    onChange={(e) => setFormData({ ...formData, username: e.target.value })}
                                    required
                                    className="focus:outline-none w-full bg-transparent text-ink-1"
                                />
                            </div>
                        </div>

                        <div className="input-wrap flex flex-col gap-1.5">
                            <label className="input-label">MẬT KHẨU</label>
                            <div className="input-field">
                                <Lock size={16} className="text-ink-3 shrink-0" />
                                <input
                                    type={showPassword ? 'text' : 'password'}
                                    placeholder="Mật khẩu"
                                    value={formData.password}
                                    onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                                    required
                                    className="focus:outline-none w-full bg-transparent text-ink-1"
                                />
                                <button
                                    type="button"
                                    onClick={() => setShowPassword(!showPassword)}
                                    className="text-ink-3 hover:text-ink-1 focus:outline-none ml-2 flex items-center justify-center shrink-0"
                                    tabIndex={-1}
                                    aria-label={showPassword ? 'Ẩn mật khẩu' : 'Hiện mật khẩu'}
                                >
                                    {showPassword ? <EyeOff size={16} /> : <Eye size={16} />}
                                </button>
                            </div>
                        </div>

                        <div className="input-wrap flex flex-col gap-1.5">
                            <label className="input-label">MẬT KHẨU</label>
                            <div className="input-field">
                                <Lock size={16} className="text-ink-3 shrink-0" />
                                <input
                                    type={showConfirmPassword ? 'text' : 'password'}
                                    placeholder="Mật khẩu"
                                    value={formData.confirmPassword}
                                    onChange={(e) => setFormData({ ...formData, confirmPassword: e.target.value })}
                                    required
                                    className="focus:outline-none w-full bg-transparent text-ink-1"
                                />
                                <button
                                    type="button"
                                    onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                                    className="text-ink-3 hover:text-ink-1 focus:outline-none ml-2 flex items-center justify-center shrink-0"
                                    tabIndex={-1}
                                    aria-label={showConfirmPassword ? 'Ẩn mật khẩu' : 'Hiện mật khẩu'}
                                >
                                    {showConfirmPassword ? <EyeOff size={16} /> : <Eye size={16} />}
                                </button>
                            </div>
                        </div>

                        <button
                            type="submit"
                            className="btn btn-primary w-full py-3 font-semibold rounded-lg text-sm uppercase tracking-wide mt-2"
                        >
                            TIẾP TỤC
                        </button>

                        <div className="text-center pt-4 border-t border-rule">
                            <p className="text-xs text-ink-2">
                                Đã có tài khoản?{' '}
                                <Link to="/login" className="text-primary font-semibold hover:underline">
                                    Đăng nhập
                                </Link>
                            </p>
                        </div>
                    </form>
                )}

                {/* ── STEP 2: BASIC INFO ── */}
                {step === 2 && (
                    <form onSubmit={handleNextFromStep2} className="card-body space-y-4 px-8 pb-8 pt-2">
                        <div className="input-wrap flex flex-col gap-1.5">
                            <label className="input-label">SỐ ĐIỆN THOẠI</label>
                            <div className="input-field">
                                <Phone size={16} className="text-ink-3 shrink-0" />
                                <input
                                    type="tel"
                                    placeholder="Ví dụ: 091 234 5678"
                                    value={formData.phoneNumber}
                                    onChange={(e) => setFormData({ ...formData, phoneNumber: e.target.value })}
                                    required
                                    className="focus:outline-none w-full bg-transparent text-ink-1"
                                />
                            </div>
                        </div>

                        <div className="input-wrap flex flex-col gap-1.5">
                            <label className="input-label">NGÀY SINH</label>
                            <div className="input-field">
                                <Cake size={16} className="text-ink-3 shrink-0" />
                                <input
                                    type="text"
                                    placeholder="dd/mm/yyyy"
                                    value={formData.dateOfBirth}
                                    onChange={(e) => setFormData({ ...formData, dateOfBirth: e.target.value })}
                                    required
                                    className="focus:outline-none w-full bg-transparent text-ink-1"
                                />
                                <Calendar size={16} className="text-ink-3 shrink-0 cursor-pointer" />
                            </div>
                        </div>

                        <div className="input-wrap flex flex-col gap-1.5">
                            <label className="input-label">GIỚI TÍNH</label>
                            <div className="grid grid-cols-3 gap-2.5">
                                {/* Nam */}
                                <button
                                    type="button"
                                    onClick={() => setFormData({ ...formData, gender: 'Nam' })}
                                    className={`flex items-center justify-center gap-1.5 py-2 px-3 rounded-lg border text-xs font-medium transition-all ${
                                        formData.gender === 'Nam'
                                            ? 'border-blue-500 bg-blue-50/70 text-blue-700 dark:bg-blue-950/40 dark:text-blue-300'
                                            : 'border-rule hover:bg-surface-2 dark:hover:bg-surface-dark-2 text-ink-2'
                                    }`}
                                >
                                    <svg className="w-3.5 h-3.5 text-blue-500" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
                                        <path d="M16 3h5v5" />
                                        <path d="m21 3-6.75 6.75" />
                                        <circle cx="10" cy="14" r="6" />
                                    </svg>
                                    <span>Nam</span>
                                </button>

                                {/* Nữ */}
                                <button
                                    type="button"
                                    onClick={() => setFormData({ ...formData, gender: 'Nữ' })}
                                    className={`flex items-center justify-center gap-1.5 py-2 px-3 rounded-lg border text-xs font-medium transition-all ${
                                        formData.gender === 'Nữ'
                                            ? 'border-pink-500 bg-pink-50/70 text-pink-700 dark:bg-pink-950/40 dark:text-pink-300'
                                            : 'border-rule hover:bg-surface-2 dark:hover:bg-surface-dark-2 text-ink-2'
                                    }`}
                                >
                                    <svg className="w-3.5 h-3.5 text-pink-500" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
                                        <circle cx="12" cy="9" r="6" />
                                        <path d="M12 15v7" />
                                        <path d="M9 19h6" />
                                    </svg>
                                    <span>Nữ</span>
                                </button>

                                {/* Khác */}
                                <button
                                    type="button"
                                    onClick={() => setFormData({ ...formData, gender: 'Khác' })}
                                    className={`flex items-center justify-center gap-1.5 py-2 px-3 rounded-lg border text-xs font-medium transition-all ${
                                        formData.gender === 'Khác'
                                            ? 'border-gray-500 bg-gray-100 text-ink-1 dark:bg-surface-dark-3 dark:text-ink-d1'
                                            : 'border-rule hover:bg-surface-2 dark:hover:bg-surface-dark-2 text-ink-2'
                                    }`}
                                >
                                    <svg className="w-3.5 h-3.5 text-gray-500" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
                                        <circle cx="12" cy="12" r="7" />
                                    </svg>
                                    <span>Khác</span>
                                </button>
                            </div>
                        </div>

                        <button
                            type="submit"
                            className="btn btn-primary w-full py-3 font-semibold rounded-lg text-sm uppercase tracking-wide mt-2"
                        >
                            TIẾP TỤC
                        </button>

                        <div className="text-center pt-4 border-t border-rule">
                            <p className="text-xs text-ink-2">
                                Đã có tài khoản?{' '}
                                <Link to="/login" className="text-primary font-semibold hover:underline">
                                    Đăng nhập
                                </Link>
                            </p>
                        </div>
                    </form>
                )}

                {/* ── STEP 3: PERSONALIZED PROFILE ── */}
                {step === 3 && (
                    <form onSubmit={handleCompleteStep3} className="card-body space-y-4 px-8 pb-8 pt-2">
                        <div className="input-wrap flex flex-col gap-1.5">
                            <label className="input-label">NGHÀNH HỌC</label>
                            <div className="input-field">
                                <User size={16} className="text-ink-3 shrink-0" />
                                <input
                                    type="text"
                                    placeholder="Ví dụ: Sinh viên"
                                    value={formData.major}
                                    onChange={(e) => setFormData({ ...formData, major: e.target.value })}
                                    className="focus:outline-none w-full bg-transparent text-ink-1"
                                />
                            </div>
                        </div>

                        {/* Lĩnh vực quan tâm */}
                        <div className="flex flex-col gap-1.5">
                            <label className="input-label">LĨNH VỰC QUAN TÂM (chọn nhiều)</label>
                            <div className="flex flex-wrap gap-2 pt-1">
                                {displayedInterests.map((item) => {
                                    const isSelected = formData.interests.includes(item);
                                    return (
                                        <button
                                            key={item}
                                            type="button"
                                            onClick={() => toggleInterest(item)}
                                            className={`px-3 py-1.5 rounded-full text-xs font-medium border transition-all ${
                                                isSelected
                                                    ? 'border-blue-500 bg-blue-50 text-blue-700 dark:bg-blue-950/40 dark:text-blue-300'
                                                    : 'border-rule bg-white dark:bg-surface-dark-2 text-ink-2 hover:bg-surface-2'
                                            }`}
                                        >
                                            {item}
                                        </button>
                                    );
                                })}
                            </div>
                            <div className="flex justify-end">
                                <button
                                    type="button"
                                    onClick={() => setShowAllInterests(!showAllInterests)}
                                    className="text-xs text-[#00a8e8] hover:underline font-medium inline-flex items-center gap-0.5 mt-0.5"
                                >
                                    {showAllInterests ? (
                                        <>
                                            <Minus size={12} /> Thu gọn
                                        </>
                                    ) : (
                                        <>
                                            <Plus size={12} /> Xem thêm
                                        </>
                                    )}
                                </button>
                            </div>
                        </div>

                        {/* Sở thích */}
                        <div className="flex flex-col gap-1.5">
                            <label className="input-label">SỞ THÍCH (chọn nhiều)</label>
                            <div className="flex flex-wrap gap-2 pt-1">
                                {displayedHobbies.map((item) => {
                                    const isSelected = formData.hobbies.includes(item);
                                    return (
                                        <button
                                            key={item}
                                            type="button"
                                            onClick={() => toggleHobby(item)}
                                            className={`px-3 py-1.5 rounded-full text-xs font-medium border transition-all ${
                                                isSelected
                                                    ? 'border-blue-500 bg-blue-50 text-blue-700 dark:bg-blue-950/40 dark:text-blue-300'
                                                    : 'border-rule bg-white dark:bg-surface-dark-2 text-ink-2 hover:bg-surface-2'
                                            }`}
                                        >
                                            {item}
                                        </button>
                                    );
                                })}
                            </div>
                            <div className="flex justify-end">
                                <button
                                    type="button"
                                    onClick={() => setShowAllHobbies(!showAllHobbies)}
                                    className="text-xs text-[#00a8e8] hover:underline font-medium inline-flex items-center gap-0.5 mt-0.5"
                                >
                                    {showAllHobbies ? (
                                        <>
                                            <Minus size={12} /> Thu gọn
                                        </>
                                    ) : (
                                        <>
                                            <Plus size={12} /> Xem thêm
                                        </>
                                    )}
                                </button>
                            </div>
                        </div>

                        <button
                            type="submit"
                            disabled={isLoading}
                            className={`btn btn-primary w-full py-3 font-semibold rounded-lg text-sm uppercase tracking-wide mt-2 ${
                                isLoading ? 'opacity-70 cursor-not-allowed' : ''
                            }`}
                        >
                            HOÀN THÀNH ĐĂNG KÝ
                        </button>

                        <div className="text-center pt-4 border-t border-rule">
                            <p className="text-xs text-ink-2">
                                Đã có tài khoản?{' '}
                                <Link to="/login" className="text-primary font-semibold hover:underline">
                                    Đăng nhập
                                </Link>
                            </p>
                        </div>
                    </form>
                )}
            </div>
        </div>
    );
};
