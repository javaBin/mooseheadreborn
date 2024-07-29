import {ReactNode, useEffect, useState} from "react";
import {AppContext} from "./AppContext";
import {defaultUserLogin, UserLogin, UserType} from "../ServerTypes";
import readUserFromServer from "../hooks/readUserFromServer";





export const AppContextProvider = ({children}: {children: ReactNode}) => {
    const storedAccessToken = window.localStorage.getItem("accessToken");

    const [userLogin,setUserLogin] = useState<UserLogin>(defaultUserLogin);

    useEffect(() => {
        if (storedAccessToken) {
            readUserFromServer(storedAccessToken).then((serverUserLogin) => setUserLogin(serverUserLogin))
        }
    }, []);

    useEffect(() => {
        if (userLogin.accessToken) {
            window.localStorage.setItem("accessToken", userLogin.accessToken);
        } else {
            window.localStorage.removeItem("accessToken");
        }
    }, [userLogin]);

    return (
        <AppContext.Provider value={{userLogin,setUserLogin}}>
            {children}
        </AppContext.Provider>
    );
}