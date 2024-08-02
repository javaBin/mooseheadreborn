import {RegistrationStatus, WorkshopInfoFromServer, WorkshopStatus} from "../ServerTypes";
import RegisterParticipant from "./RegisterParticipant";
import WorkshopDisplay from "./WorkshopDisplay";
import {useEffect, useState} from "react";
import workshopInfoWithUserFromServer from "../hooks/workshopInfoWithUserFromServer";
import WorkshopRegistration from "./WorkshopRegistration";
import {Alert} from "react-bootstrap";

interface WorkshopInfoProps {
    workshopId:string,
    accessToken:string|null,
}
const WorkshopRegistrationComponent: React.FC<WorkshopInfoProps> = ({workshopId,accessToken}) => {
    const [workshopInfoFromServer, setWorkshopInfoFromServer] = useState<WorkshopInfoFromServer|null>(null);
    const [errormessage, setErrormessage] = useState<string|null>(null);

    useEffect(() => {
        workshopInfoWithUserFromServer(workshopId,accessToken).then(resultFromServer => {
                setWorkshopInfoFromServer(resultFromServer);
            }
        ).catch(error => {
            setErrormessage(error)}
        );
    }, [workshopId, accessToken]);

    const openForRegistration = ((workshopInfoFromServer?.workshop?.workshopstatus === WorkshopStatus.OPEN || workshopInfoFromServer?.workshop?.workshopstatus === WorkshopStatus.FULL));

    return (<div>
        <h1>Registration</h1>
        {errormessage && <Alert variant={"danger"}>{errormessage}</Alert>}
        {workshopInfoFromServer?.workshop && <WorkshopDisplay workshop={workshopInfoFromServer.workshop} displayLink={false}/>}

        {(openForRegistration && workshopInfoFromServer && workshopInfoFromServer.registrationStatus === RegistrationStatus.NOT_LOGGED_IN) && <RegisterParticipant/>}
        {(openForRegistration && accessToken !== null && workshopInfoFromServer && workshopInfoFromServer.registrationStatus !== RegistrationStatus.NOT_LOGGED_IN) && <WorkshopRegistration workshopInfoFromServer={workshopInfoFromServer} accessToken={accessToken}/>}
    </div>);
}

export default WorkshopRegistrationComponent;