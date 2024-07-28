import {AddRegistrationInput, RegistrationStatus, WorkshopInfoFromServer, WorkshopStatus} from "../ServerTypes";
import {Alert, Button} from "react-bootstrap";
import addRegistrationToServer from "../hooks/addRegistrationToServer";
import {useState} from "react";
import WorkshopCancellation from "./WorkshopCancellation";


interface WorkshopRegistrationProps {
    workshopInfoFromServer:WorkshopInfoFromServer
    accessToken:string,
}

const WorkshopRegistration : React.FC<WorkshopRegistrationProps> = ({workshopInfoFromServer,accessToken}) => {
    const [registrationStatus,setRegistrationStatus] = useState<RegistrationStatus>(workshopInfoFromServer.registrationStatus);
    const [registrationId,setRegistrationId] = useState<string|null>(workshopInfoFromServer.registrationId);
    const [showConfirmCancelMessage,setShowConfirmCancelMessage] = useState<boolean>(false);
    const [errormessage,setErrormessage] = useState<string|null>(null);
    const onRegisterClick = () => {
        setShowConfirmCancelMessage(false);
        const addRegistationInput:AddRegistrationInput = {
            accessToken: accessToken,
            workshopId: workshopInfoFromServer.workshop.id,
            numParticipants: 1
        }
        addRegistrationToServer(addRegistationInput).then(addRegistrationOutput => {
            setRegistrationStatus(addRegistrationOutput.registrationStatus);
            setRegistrationId(addRegistrationOutput.registrationId);
        }).catch(errorFromServer => setErrormessage(errorFromServer));
    }
    const onRegistrationCancelled:(registrationStatus:RegistrationStatus)=>void = (registrationStatus) => {
        setRegistrationStatus(registrationStatus);
        setRegistrationId(null);
        setShowConfirmCancelMessage(true);
    }
    return (<div>
        {(workshopInfoFromServer.workshop.workshopstatus === WorkshopStatus.NOT_OPEN) && <p>Workshop not open for registration yet. Opens {workshopInfoFromServer.workshop.opensAt}.</p>}
        {(workshopInfoFromServer.workshop.workshopstatus === WorkshopStatus.CLOSED) && <p>Workshop not open for registration anymore.</p>}
        {(!showConfirmCancelMessage && workshopInfoFromServer.workshop.workshopstatus === WorkshopStatus.OPEN && (registrationStatus === RegistrationStatus.NOT_REGISTERED || registrationStatus == RegistrationStatus.CANCELLED)) && <Button variant={"primary"} onClick={onRegisterClick}>Reserve a spot on workshop</Button>}
        {errormessage && <Alert variant="danger">{errormessage}</Alert>}
        {(registrationId) && <WorkshopCancellation accessToken={accessToken} registrationId={registrationId} onRegistrationCancelled={onRegistrationCancelled} registrationStatus={registrationStatus}/>}
        {showConfirmCancelMessage && <Alert variant={"info"}>Registration has been cancelled</Alert> }
    </div>);
};

export default WorkshopRegistration;