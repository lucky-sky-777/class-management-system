import { RouterProvider } from 'react-router-dom';
import { router } from '../router';
import { AppBootstrap } from './AppBootstrap';

/**
 * Global application provider that wraps the app with various contexts (Router, Theme, etc.)
 */
export const AppProvider = () => {
    return (
        <AppBootstrap>
            <RouterProvider router={router} />
        </AppBootstrap>
    );
};
