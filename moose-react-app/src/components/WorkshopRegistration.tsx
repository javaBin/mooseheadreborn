import {AddRegistrationInput, RegistrationStatus, WorkshopInfoFromServer, WorkshopStatus} from "../ServerTypes";
import {Button} from "react-bootstrap";
import addRegistrationToServer from "../hooks/addRegistrationToServer";
import {useState} from "react";

interface WorkshopRegistrationProps {
    workshopInfoFromServer:WorkshopInfoFromServer
    accessToken:string,
}

const WorkshopRegistration : React.FC<WorkshopRegistrationProps> = ({workshopInfoFromServer,accessToken}) => {
    const [registrationStatus,setRegistrationStatus] = useState<RegistrationStatus>(workshopInfoFromServer.registrationStatus);
    const onRegisterClick = () => {
        const addRegistationInput:AddRegistrationInput = {
            accessToken: accessToken,
            workshopId: workshopInfoFromServer.workshop.id,
            numParticipants: 1
        }
        addRegistrationToServer(addRegistationInput).then(addRegistrationToServerResult => {
            if (addRegistrationToServerResult.addRegistrationOutput) {
                setRegistrationStatus(addRegistrationToServerResult.addRegistrationOutput.registrationStatus);
            }
        })
    }
    return (<div>
        {(workshopInfoFromServer.workshop.workshopstatus === WorkshopStatus.NOT_OPEN) && <p>Workshop not open for registration yet. Opens {workshopInfoFromServer.workshop.opensAt}.</p>}
        {(workshopInfoFromServer.workshop.workshopstatus === WorkshopStatus.CLOSED) && <p>Workshop not open for registration anymore.</p>}
        {(workshopInfoFromServer.workshop.workshopstatus === WorkshopStatus.OPEN && registrationStatus === RegistrationStatus.NOT_REGISTERED) && <Button onClick={onRegisterClick}>Reserve a spot on workshop</Button>}
        {(registrationStatus === RegistrationStatus.REGISTERED) && <p>You are registered on this workshop</p>}
    </div>);
};

export default WorkshopRegistration;