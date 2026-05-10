import React, { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import { useActivity } from "@features/activity/hooks/useActivity";
import { useUserSummary } from "@features/activity/hooks/useUserSummary";
import { ActivityCard } from "@features/activity/components/ActivityCard";
import { ActivityFormModal } from "@features/activity/components/ActivityFormModal";
import { RegistrationPanel } from "@features/activity/components/RegistrationPanel";
import { SummaryTable } from "@features/activity/components/SummaryTable";
import type { Activity, CreateActivityDTO } from "@features/activity/types";
import { Plus } from "lucide-react";
import { classDiagramAPI } from "@features/classDiagram/api";


type Tab = "ACTIVITIES" | "SUMMARY";
const Tab = {
    ACTIVITIES: "ACTIVITIES",
    SUMMARY: "SUMMARY",
} as const;

export const ActivityPage: React.FC = () => {
    const { classId } = useParams<{ classId: string }>();
    const cid = Number(classId);

    const [activeTab, setActiveTab] = useState<Tab>("ACTIVITIES");

    // State cho Modal Form (Tạo/Sửa)
    const [isFormOpen, setIsFormOpen] = useState(false);
    const [editingActivity, setEditingActivity] = useState<Activity | null>(null);
    const [isSubmitting, setIsSubmitting] = useState(false);

    // State cho Registration Panel
    const [isRegOpen, setIsRegOpen] = useState(false);
    const [selectedActivity, setSelectedActivity] = useState<Activity | null>(null);

    const [members, setMembers] = useState<{ id: string; name: string }[]>([]);

    // Hooks
    const { activities, isLoading: actLoading, error: actError, createActivity, updateActivity, deleteActivity } = useActivity(cid);
    const { summaries, isLoading: sumLoading, error: sumError } = useUserSummary(cid);

    // Fetch members to map name in summary table
    useEffect(() => {
        if (cid && activeTab === "SUMMARY") {
            classDiagramAPI.getMembers(cid.toString()).then(data => {
                setMembers(data);
            });
        }
    }, [cid, activeTab]);

    const userNames = new Map<number, string>(members.map(m => [Number(m.id), m.name]));

    // Handlers
    const handleOpenCreate = () => {
        setEditingActivity(null);
        setIsFormOpen(true);
    };

    const handleOpenEdit = (act: Activity) => {
        setEditingActivity(act);
        setIsFormOpen(true);
    };

    const handleDelete = async (id: number) => {
        if (!window.confirm("Bạn có chắc muốn xóa hoạt động này?")) return;
        await deleteActivity(id);
    };

    const handleFormSubmit = async (dto: CreateActivityDTO) => {
        setIsSubmitting(true);
        let success = false;
        if (editingActivity) {
            success = await updateActivity(editingActivity.id as number, dto);
        } else {
            success = await createActivity(dto);
        }
        setIsSubmitting(false);
        return success;
    };

    const handleViewRegistrations = (act: Activity) => {
        setSelectedActivity(act);
        setIsRegOpen(true);
    };

    return (
        <div className="space-y-6 animate-fade-in relative pb-24">
            {/* Tabs */}
            <div className="flex items-center gap-6 border-b border-rule pb-2">
                <button
                    onClick={() => setActiveTab("ACTIVITIES")}
                    className={`pb-3 text-sm font-bold border-b-2 transition-all px-2 ${activeTab === "ACTIVITIES"
                        ? "border-warm-400 text-warm-600"
                        : "border-transparent text-ink-3 hover:text-ink-1"
                        }`}
                >
                    Danh sách hoạt động
                </button>
                <button
                    onClick={() => setActiveTab("SUMMARY")}
                    className={`pb-3 text-sm font-bold border-b-2 transition-all px-2 ${activeTab === "SUMMARY"
                        ? "border-warm-400 text-warm-600"
                        : "border-transparent text-ink-3 hover:text-ink-1"
                        }`}
                >
                    Thống kê rèn luyện
                </button>
            </div>

            {/* Error alerts */}
            {((actError && activeTab == Tab.ACTIVITIES ) || (sumError && activeTab == Tab.SUMMARY)) && (
                <div className="p-4 bg-ink-red-fill border border-ink-red-border text-ink-red-text rounded-lg text-sm font-medium">
                    {actError || sumError}
                </div>
            )}

            {/* Content: Activities */}
            {activeTab === "ACTIVITIES" && (
                <div className="space-y-4">
                    {actLoading ? (
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                            {[1, 2].map(i => <div key={i} className="h-40 skeleton rounded-xl" />)}
                        </div>
                    ) : activities.length === 0 ? (
                        <div className="py-20 text-center space-y-4 bg-surface-2/50 rounded-xl border-2 border-dashed border-rule">
                            <p className="text-4xl text-ink-4">◉</p>
                            <div>
                                <p className="font-serif font-bold text-lg text-ink-1">Chưa có hoạt động nào</p>
                                <p className="text-sm text-ink-3">Tạo hoạt động mới để các thành viên đăng ký.</p>
                            </div>
                            <button onClick={handleOpenCreate} className="btn btn-warm btn-sm mt-2">
                                + Tạo hoạt động
                            </button>
                        </div>
                    ) : (
                        <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
                            {activities.map(act => (
                                <ActivityCard
                                    key={act.id}
                                    activity={act}
                                    onEdit={handleOpenEdit}
                                    onDelete={handleDelete}
                                    onViewRegistrations={handleViewRegistrations}
                                />
                            ))}
                        </div>
                    )}
                </div>
            )}

            {/* Content: Summary */}
            {activeTab === "SUMMARY" && (
                <div className="card p-4 md:p-6 shadow-sm">
                    <SummaryTable
                        summaries={summaries}
                        isLoading={sumLoading}
                        userNames={userNames}
                    />
                </div>
            )}

            {/* Floating Action Button (for ACTIVITIES tab) */}
            {activeTab === "ACTIVITIES" && (
                <div className="fixed bottom-8 right-6 md:right-10 z-raised">
                    <button
                        onClick={handleOpenCreate}
                        className="btn btn-warm h-14 px-6 rounded-full shadow-2xl shadow-warm-400/30 flex items-center gap-2 group hover:scale-105 transition-transform"
                    >
                        <Plus className="w-5 h-5 group-hover:rotate-90 transition-transform duration-300" />
                        <span className="font-bold">Tạo hoạt động</span>
                    </button>
                </div>
            )}

            {/* Modals */}
            <ActivityFormModal
                isOpen={isFormOpen}
                onClose={() => setIsFormOpen(false)}
                onSubmit={handleFormSubmit}
                editingActivity={editingActivity}
                isSubmitting={isSubmitting}
            />

            <RegistrationPanel
                isOpen={isRegOpen}
                onClose={() => setIsRegOpen(false)}
                activity={selectedActivity}
            />
        </div>
    );
};
