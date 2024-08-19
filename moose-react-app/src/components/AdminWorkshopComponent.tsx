import {AdminWorkshopType, ChangeCapacityType, UserLogin} from "../ServerTypes";
import AdminWorkshopRegistrationComponent from "./AdminWorkshopRegistrationComponent";
import {Alert, Button, Col, Form, Modal, Row} from "react-bootstrap";
import {FormEvent, useRef, useState} from "react";
import changeCapacityForWorkshop from "../hooks/changeCapacityForWorkshop";

interface AdminWorksopProps {
    workshop:AdminWorkshopType,
    userLogin:UserLogin
}

const AdminWorkshopComponent = ({workshop : inputWorkshop,userLogin}:AdminWorksopProps) => {
    const [showDetails,setShowDetails] = useState<boolean>(true);
    const [showParticipants,setShowParticipants] = useState<boolean>(true);
    const [showCopied,setShowCopied] = useState<boolean>(false);
    const [showCapacityUpdated,setShowCapacityUpdated] = useState<boolean>(false);
    const updatedCapacityRef = useRef<HTMLInputElement>(null);
    const [capacityErrormessage,setCapacityErrormessage] = useState<string|null>(null);
    const [workshop,setWorkshop] = useState<AdminWorkshopType>(inputWorkshop);

    const onClickCopy = () => {
        const emailText:string = workshop.registrationList.map((p) => p.email).join(",");
        navigator.clipboard.writeText(emailText).then(() => {
            setShowCopied(true);
        })
    };

    const handleUpdateCapacity = (event:FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        const valnum:number = Number(updatedCapacityRef.current?.value || "");
        if (isNaN(valnum)) {
            setCapacityErrormessage("Need to enter a capacity");
            return
        }
        if (!userLogin.accessToken) {
            setCapacityErrormessage("Not logged in");
            return;
        }
        const changeCapacityInput:ChangeCapacityType = {
              accessToken: userLogin.accessToken,
            workshopId: workshop.id,
            capacity: valnum
        };
        setCapacityErrormessage(null);

        changeCapacityForWorkshop(changeCapacityInput).then((updatedWorkshop:AdminWorkshopType) => {
            setWorkshop(updatedWorkshop);
            setShowCapacityUpdated(true);
        }).catch(errorFromServer => setCapacityErrormessage(errorFromServer));


    }

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
            <Form onSubmit={handleUpdateCapacity}>
                <Row className="align-items-center">
                    <Col xs="auto">
                        <Form.Group controlId="formInlineEmail">
                            <Form.Control type="number" placeholder="Updated capacity" ref={updatedCapacityRef}/>
                        </Form.Group>
                    </Col>
                    <Col xs="auto">
                        <Button type="submit">Update capacity</Button>
                    </Col>
                </Row>
            </Form>
            {capacityErrormessage && <Alert variant={"danger"}>{capacityErrormessage}</Alert>}
            {showCapacityUpdated && <Col md={2}>
                <Alert variant={"light"} dismissible={true} onClose={() => setShowCapacityUpdated(false)}>Capacity was updated</Alert>
            </Col>}
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