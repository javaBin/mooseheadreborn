import {Button, Container, Nav, Navbar} from "react-bootstrap";
import React, {useContext} from "react";
import {AppContext} from "../context/AppContext";
import {defaultUserLogin, UserLogin, UserType} from "../ServerTypes";


const TopNavbarComponent = () => {
    const appContext = useContext(AppContext);


    const userLogin:UserLogin|null = appContext?.userLogin ||null;
    //const [currentUserLogin,setCurrentUserLogin] = useState<UserLogin|null>(userLogin);
    const setUserLogin = appContext?.setUserLogin
    const onForgetClick = () => {
        if (setUserLogin) {
            setUserLogin(defaultUserLogin);
        }
    }
    return (<Navbar bg="primary" data-bs-theme="dark">
            <Container>
                <Navbar.Brand href="/">JavaZone Kids Workshop registration</Navbar.Brand>
                {(userLogin?.userType === UserType.USER || userLogin?.userType === UserType.ADMIN) && <Nav>
                    <Navbar.Text>{userLogin.name} ({userLogin.email})</Navbar.Text>
                    <Button variant={"dark"} onClick={onForgetClick}>Forget me</Button>

                </Nav>}
            </Container>
        </Navbar>
    );
};

export default TopNavbarComponent;