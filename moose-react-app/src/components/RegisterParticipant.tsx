import {AddParticipantInput} from "../ServerTypes";
import {Form, Button, Alert} from "react-bootstrap";
import { useRef, useState } from "react";

interface RegisterParticipantProps {
    onRegisterParticipant:(addParticipantInput:AddParticipantInput)=>Promise<string|null>,
}

const RegisterParticipant:React.FC<RegisterParticipantProps> = ({onRegisterParticipant}) => {
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
            onRegisterParticipant(addParticipantInput)
                .then(servermessage => {
                    setErrormessage(servermessage);
                });
        }
    }
    return (<Form>
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

    </Form>);
}

export default RegisterParticipant;