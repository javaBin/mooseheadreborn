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
    const [numRegistered, setNumRegistered] = useState<number|null>(workshopInfoFromServer.numRegistered);
    const [showConfirmCancelMessage,setShowConfirmCancelMessage] = useState<boolean>(false);
    const [errormessage,setErrormessage] = useState<string|null>(null);
    const numParticipantsRef = useRef<HTMLSelectElement>(null);

    const [displayNotConfirmedErrorOne,setDisplayConfirmedErrorOne] = useState<boolean>(false)
    const confirmOneRef = useRef<HTMLInputElement>(null);

    const [displayNotConfirmedErrorTwo,setDisplayConfirmedErrorTwo] = useState<boolean>(false)
    const confirmTwoRef = useRef<HTMLInputElement>(null);




    const doSelectParticipants:boolean = (workshopInfoFromServer.workshop.registerLimit > 1);
    const doCheckConfirm:boolean = !doSelectParticipants;


    const onRegisterClick = () => {
        setDisplayConfirmedErrorOne(false);
        setDisplayConfirmedErrorTwo(false);
        setShowConfirmCancelMessage(false);

        if (doCheckConfirm && confirmOneRef.current && !confirmOneRef.current.checked) {
            setDisplayConfirmedErrorOne(true);
            return;
        }
        if (doCheckConfirm && confirmTwoRef.current && !confirmTwoRef.current.checked) {
            setDisplayConfirmedErrorTwo(true);
            return;
        }
        const numbPartSelected = doSelectParticipants ? Number(numParticipantsRef.current?.value) : 1;
        const addRegistationInput:AddRegistrationInput = {
            accessToken: accessToken,
            workshopId: workshopInfoFromServer.workshop.id,
            numParticipants: numbPartSelected
        }
        addRegistrationToServer(addRegistationInput).then(addRegistrationOutput => {
            setRegistrationStatus(addRegistrationOutput.registrationStatus);
            setRegistrationId(addRegistrationOutput.registrationId);
            if (doSelectParticipants) {
                setNumRegistered(numbPartSelected)
            }
        }).catch(errorFromServer => setErrormessage(errorFromServer));
    };
    const onRegistrationCancelled:(registrationStatus:RegistrationStatus)=>void = (registrationStatus) => {
        setRegistrationStatus(registrationStatus);
        setRegistrationId(null);
        setShowConfirmCancelMessage(true);
        setNumRegistered(doSelectParticipants ? Number(numParticipantsRef.current?.value) : null);
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
                {doCheckConfirm && <div>
                    <Form.Group className="mb-3">
                        <Form.Check type="checkbox" label="I am aware that I need to have a valid JavaZone ticket in order to enter the workshop and that I will need to show up at Oslo Spektrum since it is not possible to follow the workshop digitally."
                            ref={confirmOneRef}/>
                        {displayNotConfirmedErrorOne && <Form.Control.Feedback type="invalid" style={{ display: 'block' }}>
                            You need to confirm by clicking the checkbox
                        </Form.Control.Feedback>}
                    </Form.Group>
                    <Form.Group className="mb-3">
                        <Form.Check type="checkbox" label="I acknowledge that, since some of the workshops are happening in parallell, it is my responsibility to make sure that I do not register for overlapping workshops - in which case the organizers have a right to cancel my registration for one or more of the workshops."
                                    ref={confirmTwoRef}/>
                        {displayNotConfirmedErrorTwo && <Form.Control.Feedback type="invalid" style={{ display: 'block' }}>
                            You need to confirm by clicking the checkbox
                        </Form.Control.Feedback>}
                    </Form.Group>
                </div>}
                <Button variant={"primary"} onClick={onRegisterClick}>Register for workshop</Button>
            </Form>
        }
        {errormessage && <Alert variant="danger">{errormessage}</Alert>}
        {(registrationId) && <WorkshopCancellation accessToken={accessToken} registrationId={registrationId} onRegistrationCancelled={onRegistrationCancelled} registrationStatus={registrationStatus} numRegistered={numRegistered}/>}
        {showConfirmCancelMessage && <Alert variant={"info"}>Registration has been cancelled</Alert> }
    </div>);
};

export default WorkshopRegistration;