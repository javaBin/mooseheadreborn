import {Alert, Container} from "react-bootstrap";
import {useParams} from "react-router-dom";
import {useEffect, useState} from "react";
import activateUserOnServer from "../hooks/activateUserOnServer";
import WorkshopRegistrationComponent from "../components/WorkshopRegistrationComponent";

function ActivateParticipantPage() {
    const { registerKey} = useParams<{registerKey: string}>();
    const [accessToken, setAccessToken] = useState<string | null>(null);
    const [errormessage, setErrormessage] = useState<string | null>(null);
    const [displayRegisterComponent, setDisplayRegisterComponent] = useState<boolean>(false);

    const workshopId = window.localStorage.getItem("currentWorkshopId");

    useEffect(() => {
        if (registerKey) {
            activateUserOnServer(registerKey).then(accessKeyFromServer => {
               window.localStorage.setItem("accessToken",accessKeyFromServer);
               setAccessToken(accessKeyFromServer);
               setDisplayRegisterComponent(true);
            }).catch(errorFromServer => setErrormessage(errorFromServer));
        } else {
            setErrormessage("Unknown page");
        }
    }, []);

    return (<Container>
        {errormessage && <Alert variant="danger">{errormessage}</Alert>}
        {!accessToken && !errormessage && <div>Loading</div>}
        {accessToken && workshopId && <WorkshopRegistrationComponent workshopId={workshopId} accessToken={accessToken}/>}
        {accessToken && !workshopId && <div>User activated</div>}
    </Container>)
}

export default ActivateParticipantPage;