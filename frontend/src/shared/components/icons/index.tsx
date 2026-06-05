import googleLogo from "../../../assets/google_logo.png";
import mezonLogo from "../../../assets/mezon_logo.png";

export const GoogleIcon = () => {
    return (
        <img
            src={googleLogo}
            alt="Google"
            width={24}
            height={24}
            className="rounded-full object-cover"
        />
    );
}

export const MezonIcon = () => {
    return (
        <img
            src={mezonLogo}
            alt="Mezon"
            width={18}
            height={18}
            className="rounded-full object-cover"
        />
    );
};