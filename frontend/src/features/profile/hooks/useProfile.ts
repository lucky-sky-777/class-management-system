// src/features/profile/hooks/useProfile.ts
import { useState, useEffect } from "react";
import { profileAPI } from "@/features/profile/api";
import type { UserProfile, ProfileDocument } from "@/features/profile/types";

export const useProfile = () => {
  const [profile, setProfile] = useState<UserProfile | null>(null);
  const [documents, setDocuments] = useState<ProfileDocument[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      setIsLoading(true);
      try {
        const [profileData, docsData] = await Promise.all([
          profileAPI.getProfile(),
          profileAPI.getUserDocuments()
        ]);
        setProfile(profileData);
        setDocuments(docsData);
      } catch (error) {
        console.error("Lỗi tải profile:", error);
      } finally {
        setIsLoading(false);
      }
    };
    fetchData();
  }, []);

  return { profile, documents, isLoading };
};