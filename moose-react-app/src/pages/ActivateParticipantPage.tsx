import {Alert, Container} from "react-bootstrap";
import {useParams,useNavigate} from "react-router-dom";
import {Dispatch, SetStateAction, useContext, useEffect, useState} from "react";
import activateUserOnServer from "../hooks/activateUserOnServer";
import {AppContext} from "../context/AppContext";
import {UserLogin} from "../ServerTypes";

function ActivateParticipantPage() {
    const { registerKey} = useParams<{registerKey: string}>();
    const [errormessage, setErrormessage] = useState<string | null>(null);

    const workshopId = window.localStorage.getItem("currentWorkshopId");
    const appContext = useContext(AppContext);
    const setUserLogin: Dispatch<SetStateAction<UserLogin>>|undefined = appContext?.setUserLogin;
    const navigate = useNavigate();

    useEffect(() => {
        if (registerKey) {
            activateUserOnServer(registerKey).then(userLoginFromServer => {
               if (setUserLogin) {
                   setUserLogin(userLoginFromServer);
               }
               if (workshopId) {
                   navigate("/workshop/" + workshopId);
               } else {
                   navigate("/");
               }
           }).catch(errorFromServer => setErrormessage(errorFromServer));
        } else {
            setErrormessage("Unknown page");
        }
    }, [registerKey,setUserLogin]);

    return (<Container>
        {errormessage && <Alert variant="danger">{errormessage}</Alert>}
        {!errormessage && <div>Loading</div>}
    </Container>)
}

export default ActivateParticipantPage;