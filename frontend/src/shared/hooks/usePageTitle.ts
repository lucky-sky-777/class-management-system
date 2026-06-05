import { useEffect } from 'react';
import { useMatches } from 'react-router-dom';

const DEFAULT_TITLE = 'Class Management';

type RouteHandle = {
    title?: string;
};

export const usePageTitle = () => {
    const matches = useMatches();

    useEffect(() => {
        const currentRoute = [...matches]
            .reverse()
            .find(match => (match.handle as RouteHandle)?.title);

        const pageTitle = (currentRoute?.handle as RouteHandle)?.title;

        document.title = pageTitle
            ? `${pageTitle} | ${DEFAULT_TITLE}`
            : DEFAULT_TITLE;
    }, [matches]);
};