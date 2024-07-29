import {UserLogin} from "../ServerTypes";
import React, {Dispatch, SetStateAction} from "react";


interface UserContextType {
    userLogin: UserLogin;
    setUserLogin: Dispatch<SetStateAction<UserLogin>>
}


export const AppContext = React.createContext<UserContextType|undefined>(undefined);