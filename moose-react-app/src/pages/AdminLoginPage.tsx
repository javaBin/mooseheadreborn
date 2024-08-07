import {Alert, Button, Container} from "react-bootstrap";
import {Dispatch, SetStateAction, useContext, useRef, useState} from "react";
import {AppContext} from "../context/AppContext";
import {AdminLoginInput, UserLogin} from "../ServerTypes";
import adminLoginToServer from "../hooks/adminLoginToServer";
import {useNavigate} from "react-router-dom";

const AdminLoginPage = () => {
    const appContext = useContext(AppContext);
    const setUserLogin: Dispatch<SetStateAction<UserLogin>>|undefined = appContext?.setUserLogin;
    const passwordRef = useRef<HTMLInputElement>(null);
    const [errormessage,setErrormessage] = useState<string|null>(null);
    const navigate = useNavigate();

    const onLoginClick = () => {
        if (!setUserLogin) {
            setErrormessage("Internal error missing userlogin")
            return;
        }
        setErrormessage(null);
        const password = passwordRef.current?.value;
        if (!password) {
            setErrormessage("Missing password");
            return
        }
        const adminLoginInput:AdminLoginInput = {
            password: password
        };
        adminLoginToServer(adminLoginInput).then((userLoginFromServer) => {
            console.log("Setting user login",userLoginFromServer)
            setUserLogin(userLoginFromServer);
            navigate("/admin");
        }).catch(errorFromServer => setErrormessage(errorFromServer));

    }

    return (<Container>
        <h1>Admin login</h1>
        <input type={"password"} ref={passwordRef}/>
        <Button variant={"primary"} onClick={onLoginClick}>Login</Button>
        {errormessage && <Alert variant={"danger"}>{errormessage}</Alert> }
    </Container>)
};

export default AdminLoginPage;