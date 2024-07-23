import {RegistrationStatus, WorkshopInfoFromServer} from "../ServerTypes";
import RegisterParticipant from "./RegisterParticipant";
import WorkshopDisplay from "./WorkshopDisplay";
import {useEffect, useState} from "react";
import workshopInfoWithUserFromServer from "../hooks/workshopInfoWithUserFromServer";
import WorkshopRegistration from "./WorkshopRegistration";

interface WorkshopInfoProps {
    workshopId:string,
    accessToken:string|null,
}
const WorkshopRegistrationComponent: React.FC<WorkshopInfoProps> = ({workshopId,accessToken}) => {
    const [workshopInfoFromServer, setWorkshopInfoFromServer] = useState<WorkshopInfoFromServer|null>(null);

    useEffect(() => {
        workshopInfoWithUserFromServer(workshopId,accessToken).then(resultFromServer => {
                if (resultFromServer.workshopInfoFromServer) {
                    setWorkshopInfoFromServer(resultFromServer.workshopInfoFromServer);
                }
            }
        )
    }, []);

    return (<div>
        <h1>Registration</h1>
        {workshopInfoFromServer?.workshop && <WorkshopDisplay workshop={workshopInfoFromServer.workshop} displayLink={false}/>}

        {(workshopInfoFromServer && workshopInfoFromServer.registrationStatus === RegistrationStatus.NOT_LOGGED_IN) && <RegisterParticipant/>}
        {(accessToken !== null && workshopInfoFromServer && workshopInfoFromServer.registrationStatus !== RegistrationStatus.NOT_LOGGED_IN) && <WorkshopRegistration workshopInfoFromServer={workshopInfoFromServer} accessToken={accessToken}/>}
    </div>);
}

export default WorkshopRegistrationComponent;