import {AdminWorkshopType} from "../ServerTypes";
import AdminWorkshopRegistrationComponent from "./AdminWorkshopRegistrationComponent";
import {Alert, Button, Col, Modal} from "react-bootstrap";
import {useState} from "react";

interface AdminWorksopProps {
    workshop:AdminWorkshopType
}

const AdminWorkshopComponent = ({workshop}:AdminWorksopProps) => {
    const [showDetails,setShowDetails] = useState<boolean>(true);
    const [showParticipants,setShowParticipants] = useState<boolean>(true);
    const [showCopied,setShowCopied] = useState<boolean>(false);

    const onClickCopy = () => {
        const emailText:string = workshop.registrationList.map((p) => p.email).join(",");
        navigator.clipboard.writeText(emailText).then(() => {
            setShowCopied(true);
        })
    };

    return (<div>
        <h2>{workshop.name}&nbsp;
            {showDetails && <Button variant={"dark"} size={"sm"} onClick={() => setShowDetails(false)}>Hide</Button>}
            {!showDetails && <Button variant={"info"} size={"sm"} onClick={() => setShowDetails(true)}>Show</Button>}
        </h2>
        {showDetails && <div>
            <p>Type: {workshop.workshopType}</p>
            <p>Status: {workshop.workshopstatus}</p>
            <p>Opens At: {workshop.opensAt}</p>
            <p>Register Limit: {workshop.registerLimit}</p>
            <p>Capacity: {workshop.capacity}</p>
            <p>Seats Taken: {workshop.seatsTaken}</p>
            <p>Waiting: {workshop.waitingSize}</p>
            <h3>Participants&nbsp;
                {showParticipants &&
                    <Button variant={"dark"} size={"sm"} onClick={() => setShowParticipants(false)}>Hide</Button>}
                {!showParticipants &&
                    <Button variant={"info"} size={"sm"} onClick={() => setShowParticipants(true)}>Show</Button>}
                &nbsp;<Button variant={"outline-primary"} onClick={onClickCopy}>Copy emails to clipboard</Button>
            </h3>
            {showCopied && <Col md={2}>
                <Alert variant={"light"} dismissible={true} onClose={() => setShowCopied(false)}>Copied to clipboard</Alert>
            </Col>}
            {showParticipants && <div>
                {workshop.registrationList.map((registration, partIndex) =>
                    <AdminWorkshopRegistrationComponent registration={registration} key={registration.id}
                                                        participantNumber={partIndex + 1}/>
                )}
                {workshop.registrationList.length === 0 && <div>No registrations yet</div>}
            </div>}
        </div>}
    </div>);
};

export default AdminWorkshopComponent;