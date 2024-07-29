import {Alert, Container} from "react-bootstrap";
import {useParams} from "react-router-dom";
import {Dispatch, SetStateAction, useContext, useEffect, useState} from "react";
import activateUserOnServer from "../hooks/activateUserOnServer";
import WorkshopRegistrationComponent from "../components/WorkshopRegistrationComponent";
import {AppContext} from "../context/AppContext";
import {UserLogin} from "../ServerTypes";

function ActivateParticipantPage() {
    const { registerKey} = useParams<{registerKey: string}>();
    const [accessToken, setAccessToken] = useState<string | null>(null);
    const [errormessage, setErrormessage] = useState<string | null>(null);

    const workshopId = window.localStorage.getItem("currentWorkshopId");
    const appContext = useContext(AppContext);
    const setUserLogin: Dispatch<SetStateAction<UserLogin>>|undefined = appContext?.setUserLogin;

    useEffect(() => {
        if (registerKey) {
            activateUserOnServer(registerKey).then(userLoginFromServer => {
               setAccessToken(userLoginFromServer.accessToken);
               if (setUserLogin) {
                   setUserLogin(userLoginFromServer);
               }
           }).catch(errorFromServer => setErrormessage(errorFromServer));
        } else {
            setErrormessage("Unknown page");
        }
    }, [registerKey,setUserLogin]);

    return (<Container>
        {errormessage && <Alert variant="danger">{errormessage}</Alert>}
        {!accessToken && !errormessage && <div>Loading</div>}
        {accessToken && workshopId && <WorkshopRegistrationComponent workshopId={workshopId} accessToken={accessToken}/>}
        {accessToken && !workshopId && <div>User activated</div>}
    </Container>)
}

export default ActivateParticipantPage;