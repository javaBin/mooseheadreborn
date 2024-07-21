import {AddParticipantInput} from "../ServerTypes";
import {Form, Button, Alert} from "react-bootstrap";
import { useRef, useState } from "react";
import registerParticipantToserver from "../hooks/registerParticipantToServer";

enum RegisterParticipantState {
    STARTING,
    FORM,
    DONE
}

const RegisterParticipant = () => {
    const [registerParticipantState,setRegisterParticipantState] = useState<RegisterParticipantState>(RegisterParticipantState.STARTING);
    const nameRef = useRef<HTMLInputElement>(null);
    const emailRef = useRef<HTMLInputElement>(null);
    const [errormessage, setErrormessage] = useState<string | null>(null);
    const handleSubmit: React.MouseEventHandler<HTMLButtonElement> = (event) => {
        event.preventDefault();
        setErrormessage(null);
        if (nameRef.current && emailRef.current) {
            const emailValue = emailRef.current.value;
            const nameValue = nameRef.current.value;
            if (!emailValue || emailValue.length === 0) {
                setErrormessage("Email is required");
                return
            }
            if (!nameValue || nameValue.length === 0) {
                setErrormessage("Name is required");
                return;
            }
            const addParticipantInput = {
                name: nameValue,
                email: emailValue
            }
            registerParticipantToserver(addParticipantInput)
                .then(servermessage => {
                    if (servermessage) {
                        setErrormessage(servermessage);
                    } else {
                        setRegisterParticipantState(RegisterParticipantState.DONE);
                    }
                });
        }
    }
    const onStartingButton = () => {
        setRegisterParticipantState(RegisterParticipantState.FORM);
    }
    return (<div>
        {(registerParticipantState === RegisterParticipantState.STARTING) &&
            <Button variant="info" onClick={onStartingButton}>Register</Button>}
        {(registerParticipantState === RegisterParticipantState.FORM) &&
            <Form>
                <Form.Group>
                    <Form.Label>Name</Form.Label>
                    <Form.Control type="text" placeholder="Your name" ref={nameRef}/>
                </Form.Group>
                <Form.Group>
                    <Form.Label>Email</Form.Label>
                    <Form.Control type="email" placeholder="Enter email" ref={emailRef}/>
                </Form.Group>
                {errormessage && <Alert variant="danger">{errormessage}</Alert>}
                <Button variant="primary" type="submit" onClick={handleSubmit}>
                    Submit
                </Button>

            </Form>}
        {(registerParticipantState === RegisterParticipantState.DONE) &&
            <p>Check your email and click link to continue</p>}
    </div>);
}

export default RegisterParticipant;