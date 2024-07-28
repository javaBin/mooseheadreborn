import {AddRegistrationInput, RegistrationStatus, WorkshopInfoFromServer, WorkshopStatus} from "../ServerTypes";
import {Alert, Button, Form} from "react-bootstrap";
import addRegistrationToServer from "../hooks/addRegistrationToServer";
import {ChangeEvent, useRef, useState} from "react";
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
    const numParticipantsRef = useRef<HTMLSelectElement>(null);

    const doSelectParticipants:boolean = (workshopInfoFromServer.workshop.registerLimit > 1);

    const onRegisterClick = () => {
        setShowConfirmCancelMessage(false);
        const numbPartSelected = doSelectParticipants ? Number(numParticipantsRef.current?.value) : 1;
        const addRegistationInput:AddRegistrationInput = {
            accessToken: accessToken,
            workshopId: workshopInfoFromServer.workshop.id,
            numParticipants: numbPartSelected
        }
        addRegistrationToServer(addRegistationInput).then(addRegistrationOutput => {
            setRegistrationStatus(addRegistrationOutput.registrationStatus);
            setRegistrationId(addRegistrationOutput.registrationId);
        }).catch(errorFromServer => setErrormessage(errorFromServer));
    };
    const onRegistrationCancelled:(registrationStatus:RegistrationStatus)=>void = (registrationStatus) => {
        setRegistrationStatus(registrationStatus);
        setRegistrationId(null);
        setShowConfirmCancelMessage(true);
    };
    const participantRange = doSelectParticipants ? Array.from({length: workshopInfoFromServer.workshop.registerLimit},(_,i) => i+1) : [];
    return (<div>
        {(workshopInfoFromServer.workshop.workshopstatus === WorkshopStatus.NOT_OPEN) && <p>Workshop not open for registration yet. Opens {workshopInfoFromServer.workshop.opensAt}.</p>}
        {(workshopInfoFromServer.workshop.workshopstatus === WorkshopStatus.CLOSED) && <p>Workshop not open for registration anymore.</p>}
        {(!showConfirmCancelMessage && workshopInfoFromServer.workshop.workshopstatus === WorkshopStatus.OPEN && (registrationStatus === RegistrationStatus.NOT_REGISTERED || registrationStatus == RegistrationStatus.CANCELLED)) &&
            <Form>
                {doSelectParticipants && <Form.Group>
                    <Form.Label>Number of participants</Form.Label>
                    <Form.Select ref={numParticipantsRef} style={{maxWidth: "100px", marginBottom: "5px"}}>
                        {participantRange.map((n) => <option key={n} value={n}>{n}</option>)}
                    </Form.Select>
                </Form.Group>}
                <Button variant={"primary"} onClick={onRegisterClick}>Register for workshop</Button>
            </Form>
        }
        {errormessage && <Alert variant="danger">{errormessage}</Alert>}
        {(registrationId) && <WorkshopCancellation accessToken={accessToken} registrationId={registrationId} onRegistrationCancelled={onRegistrationCancelled} registrationStatus={registrationStatus} numRegistered={workshopInfoFromServer.numRegistered}/>}
        {showConfirmCancelMessage && <Alert variant={"info"}>Registration has been cancelled</Alert> }
    </div>);
};

export default WorkshopRegistration;