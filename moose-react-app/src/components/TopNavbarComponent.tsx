import {Button, Container, Nav, Navbar} from "react-bootstrap";
import React, {useContext } from "react";
import { AppContext } from "../context/AppContext";
import {UserLogin} from "../ServerTypes";

interface TopNavbarComponentProps {
    accessToken: string|null;
}

const TopNavbarComponent:React.FC<TopNavbarComponentProps> = ({accessToken}) => {
    const appContext = useContext(AppContext);
    const userLogin:UserLogin|undefined = appContext?.userLogin
    return (<Navbar bg="primary" data-bs-theme="dark">
            <Container>
                <Navbar.Brand href="/">JavaZone Kids Workshop registration</Navbar.Brand>
                <Nav>
                    <Navbar.Text>Home {userLogin && userLogin.userType}</Navbar.Text>
                    <Nav.Link href="/">More dee</Nav.Link>

                </Nav>
            </Container>
        </Navbar>
    );
};

export default TopNavbarComponent;